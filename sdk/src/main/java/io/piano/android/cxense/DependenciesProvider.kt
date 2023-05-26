package io.piano.android.cxense

import android.content.Context
import androidx.annotation.RestrictTo
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import io.piano.android.cxense.db.DatabaseHelper
import io.piano.android.cxense.model.ApiError
import io.piano.android.cxense.model.ConversionEvent
import io.piano.android.cxense.model.EventDataRequest
import io.piano.android.cxense.model.EventStatus
import io.piano.android.cxense.model.PerformanceEvent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import timber.log.Timber
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

/**
 * Builds and provides all other classes
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
internal class DependenciesProvider private constructor(
    context: Context
) {
    private val executor: ScheduledExecutorService by lazy { Executors.newSingleThreadScheduledExecutor() }
    private val userAgentProvider: UserAgentProvider by lazy { UserAgentProvider(BuildConfig.SDK_VERSION, context) }
    private val deviceInfoProvider: DeviceInfoProvider by lazy { DeviceInfoProvider(context) }
    private val advertisingIdProvider: AdvertisingIdProvider = AdvertisingIdProvider(context, executor)
    internal val userProvider: UserProvider by lazy { UserProvider(advertisingIdProvider) }
    internal val cxenseConfiguration: CxenseConfiguration by lazy { CxenseConfiguration() }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(AuthInterceptor(cxenseConfiguration))
            .addInterceptor(SdkInterceptor(BuildConfig.SDK_NAME, BuildConfig.SDK_VERSION))
            .addInterceptor(UserAgentInterceptor(userAgentProvider))
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = if (BuildConfig.DEBUG) {
                        HttpLoggingInterceptor.Level.HEADERS
                    } else {
                        HttpLoggingInterceptor.Level.NONE
                    }
                }
            )
            .addNetworkInterceptor(RedirectLimiterInterceptor("/public/widget/click/"))
            .build()
    }

    private val moshi = Moshi.Builder()
        .add(EventDataRequest::class.java, EventsRequestAdapter())
        .add(WidgetItemAdapter())
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.SDK_ENDPOINT)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClient)
            .build()
    }

    private val cxApi: CxApi by lazy {
        retrofit.create()
    }

    private val pageViewEventConverter: PageViewEventConverter by lazy {
        PageViewEventConverter(
            moshi.adapter(
                Types.newParameterizedType(
                    Map::class.java,
                    String::class.java,
                    String::class.java
                )
            ),
            cxenseConfiguration,
            deviceInfoProvider
        )
    }
    private val performanceEventConverter: PerformanceEventConverter by lazy {
        PerformanceEventConverter(
            moshi.adapter(PerformanceEvent::class.java)
        )
    }
    private val conversionEventConverter: ConversionEventConverter by lazy {
        ConversionEventConverter(moshi.adapter(ConversionEvent::class.java))
    }

    private val errorParser: ApiErrorParser by lazy {
        ApiErrorParser(
            retrofit.responseBodyConverter(
                ApiError::class.java,
                emptyArray()
            )
        )
    }

    private val databaseHelper: DatabaseHelper by lazy { DatabaseHelper(context) }
    private val eventRepository: EventRepository by lazy {
        EventRepository(
            cxenseConfiguration,
            databaseHelper,
            listOf(
                pageViewEventConverter,
                performanceEventConverter,
                conversionEventConverter
            )
        )
    }
    private val eventsSendCallback: CxenseSdk.DispatchEventsCallback = object :
        CxenseSdk.DispatchEventsCallback {
        override fun onDispatch(statuses: List<EventStatus>) {
            statuses.mapNotNull { it.exception }.forEach {
                Timber.tag("CxenseEventCallback").e(it)
            }
        }
    }

    private val eventsSendTask: SendTask by lazy {
        SendTask(
            cxApi,
            eventRepository,
            cxenseConfiguration,
            deviceInfoProvider,
            userProvider,
            pageViewEventConverter,
            performanceEventConverter,
            errorParser,
            eventsSendCallback
        )
    }

    internal val cxenseSdk: CxenseSdk by lazy {
        CxenseSdk(
            executor,
            cxenseConfiguration,
            advertisingIdProvider,
            userProvider,
            cxApi,
            errorParser,
            moshi,
            eventRepository,
            eventsSendTask
        )
    }

    companion object {
        @JvmStatic
        @Volatile
        private var instance: DependenciesProvider? = null

        @JvmStatic
        internal fun init(context: Context): DependenciesProvider {
            if (instance == null) {
                instance = DependenciesProvider(context)
            }
            return getInstance()
        }

        @JvmStatic
        fun getInstance(): DependenciesProvider {
            checkNotNull(instance) {
                "The Cxense SDK is not initialized! Make sure to call `AppInitializer.getInstance(context)" +
                    ".initializeComponent(CxSdkInitializer::class.java)` before calling other methods."
            }
            return instance as DependenciesProvider
        }
    }
}
