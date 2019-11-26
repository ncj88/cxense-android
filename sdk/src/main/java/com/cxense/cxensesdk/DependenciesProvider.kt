package com.cxense.cxensesdk

import android.content.Context
import androidx.annotation.RestrictTo
import com.cxense.cxensesdk.db.DatabaseHelper
import com.cxense.cxensesdk.model.ApiError
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import timber.log.Timber
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

@RestrictTo(RestrictTo.Scope.LIBRARY)
internal class DependenciesProvider private constructor(
    context: Context
) {
    internal val appContext: Context by lazy { context.applicationContext }
    private val executor: ScheduledExecutorService by lazy { Executors.newSingleThreadScheduledExecutor() }
    private val userAgentProvider: UserAgentProvider by lazy { UserAgentProvider(BuildConfig.VERSION_NAME, appContext) }
    private val deviceInfoProvider: DeviceInfoProvider by lazy { DeviceInfoProvider(appContext) }
    private val advertisingIdProvider: AdvertisingIdProvider = AdvertisingIdProvider(appContext, executor)
    internal val userProvider: UserProvider by lazy { UserProvider(advertisingIdProvider) }
    internal val cxenseConfiguration: CxenseConfiguration by lazy { CxenseConfiguration() }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(AuthInterceptor(cxenseConfiguration))
            .addInterceptor(SdkInterceptor(BuildConfig.SDK_NAME, BuildConfig.VERSION_NAME))
            .addInterceptor(UserAgentInterceptor(userAgentProvider))
            .addInterceptor(HttpLoggingInterceptor().apply {
                level =
                    if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.HEADERS else HttpLoggingInterceptor.Level.NONE
            })
            .build()
    }

    private val gson: Gson by lazy {
        GsonBuilder()
            .setLenient()
            .registerTypeAdapterFactory(WidgetItemTypeAdapterFactory())
            .create()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.SDK_ENDPOINT)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()
    }

    private val cxApi: CxApi by lazy {
        retrofit.create<CxApi>()
    }

    private val pageViewEventConverter: PageViewEventConverter by lazy {
        PageViewEventConverter(
            gson,
            cxenseConfiguration,
            deviceInfoProvider
        )
    }
    private val performanceEventConverter: PerformanceEventConverter by lazy {
        PerformanceEventConverter(
            gson,
            cxenseConfiguration
        )
    }
    private val conversionEventConverter: ConversionEventConverter by lazy { ConversionEventConverter(gson) }

    private val errorParser: ApiErrorParser by lazy {
        ApiErrorParser(
            retrofit.responseBodyConverter(
                ApiError::class.java,
                emptyArray()
            )
        )
    }

    private val databaseHelper: DatabaseHelper by lazy { DatabaseHelper(appContext) }
    private val eventRepository: EventRepository by lazy {
        EventRepository(
            databaseHelper, listOf(
                pageViewEventConverter,
                performanceEventConverter,
                conversionEventConverter
            )
        )
    }
    private val eventsSendCallback: DispatchEventsCallback = { statuses ->
        statuses.mapNotNull { it.exception }.forEach {
            Timber.tag("CxenseEventCallback").e(it)
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
            gson,
            eventRepository,
            eventsSendTask
        )
    }

    companion object {
        @JvmStatic
        @Volatile
        private var instance: DependenciesProvider? = null

        @JvmStatic
        internal fun init(context: Context) {
            val v1 = instance
            if (v1 == null) {
                synchronized(this) {
                    val v2 = instance
                    if (v2 == null) {
                        instance = DependenciesProvider(context)
                    }
                }
            }
        }

        @JvmStatic
        fun getInstance(): DependenciesProvider {
            checkNotNull(instance) {
                "The Cxense SDK is not initialized! Make sure to call init before calling other methods."
            }
            return instance as DependenciesProvider
        }
    }
}
