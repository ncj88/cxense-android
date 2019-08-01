package com.cxense.cxensesdk;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import com.cxense.cxensesdk.db.DatabaseHelper;
import com.cxense.cxensesdk.model.EventRepository;
import com.cxense.cxensesdk.model.WidgetItem;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import okhttp3.Authenticator;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2018-09-17).
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public final class DependenciesProvider {
    private static final String TAG = DependenciesProvider.class.getSimpleName();

    private static DependenciesProvider instance;
    private final Context appContext;
    private final ScheduledExecutorService executor;
    private final UserAgentProvider userAgentProvider;
    private final DeviceInfoProvider deviceInfoProvider;
    private final AdvertisingIdProvider advertisingIdProvider;
    private final UserProvider userProvider;
    private final CxenseConfiguration cxenseConfiguration;
    private final CxenseAuthenticator cxenseAuthenticator;
    private final OkHttpClient okHttpClient;
    private final Gson gson;
    private final Converter.Factory converterFactory;
    private final Retrofit retrofit;
    private final ApiErrorParser errorParser;
    private final CxenseApi apiInstance;
    private final DatabaseHelper databaseHelper;
    private final EventRepository eventRepository;
    private final DispatchEventsCallback eventsSendCallback;
    private final PageViewEventConverter pageViewEventConverter;
    private final PerformanceEventConverter performanceEventConverter;
    private final ConversionEventConverter conversionEventConverter;
    private final SendTask eventsSendTask;
    private final CxenseSdk cxenseSdk;


    private DependenciesProvider(@NonNull Context context) {
        appContext = context.getApplicationContext();
        executor = Executors.newSingleThreadScheduledExecutor();
        userAgentProvider = new UserAgentProvider(getSdkVersion(), appContext, executor);
        deviceInfoProvider = new DeviceInfoProvider(appContext);
        advertisingIdProvider = new AdvertisingIdProvider(appContext, executor);
        userProvider = new UserProvider(advertisingIdProvider);
        cxenseConfiguration = new CxenseConfiguration();
        cxenseAuthenticator = new CxenseAuthenticator(cxenseConfiguration);
        Interceptor sdkInterceptor = new SdkInterceptor(getSdkName(), getSdkVersion()),
                userAgentInterceptor = new UserAgentInterceptor(userAgentProvider);
        okHttpClient = buildHttpClient(cxenseAuthenticator, sdkInterceptor, userAgentInterceptor);
        gson = buildGson();
        pageViewEventConverter = new PageViewEventConverter(gson, cxenseConfiguration, deviceInfoProvider);
        performanceEventConverter = new PerformanceEventConverter(gson, cxenseConfiguration);
        conversionEventConverter = new ConversionEventConverter(gson);
        converterFactory = GsonConverterFactory.create(gson);
        retrofit = buildRetrofit(getBaseUrl(), okHttpClient, converterFactory);
        Converter<ResponseBody, ApiError> errorConverter = retrofit.responseBodyConverter(ApiError.class, new Annotation[0]);
        errorParser = new ApiErrorParser(errorConverter);
        apiInstance = retrofit.create(CxenseApi.class);
        databaseHelper = new DatabaseHelper(appContext);
        eventRepository = new EventRepository(databaseHelper, gson, Arrays.asList(pageViewEventConverter, performanceEventConverter, conversionEventConverter));
        eventsSendCallback = statuses -> {
            for (EventStatus eventStatus : statuses) {
                if (eventStatus.exception != null) {
                    Log.e("CxenseEventCallback", String.format(Locale.getDefault(), "Error at sending event with id '%s'",
                            eventStatus.eventId), eventStatus.exception);
                }
            }
        };
        eventsSendTask = new SendTask(apiInstance, eventRepository, cxenseConfiguration, deviceInfoProvider, userProvider, gson, performanceEventConverter, errorParser, eventsSendCallback);
        cxenseSdk = new CxenseSdk(executor, cxenseConfiguration, advertisingIdProvider, userProvider, apiInstance, errorParser, gson, eventRepository, eventsSendTask);
    }

    static void init(Context context) {
        instance = new DependenciesProvider(context);
    }

    @NonNull
    public static DependenciesProvider getInstance() {
        throwIfUninitialized(instance);
        return instance;
    }

    @SuppressWarnings("WeakerAccess") // Internal API.
    protected static void throwIfUninitialized(DependenciesProvider instance) {
        if (instance == null)
            throw new IllegalStateException("The Cxense SDK is not initialized! Make sure to call init before calling other methods.");
    }

    /**
     * Gets sdk name for analytics.
     *
     * @return sdk name for analytics.
     */
    @NonNull
    private String getSdkName() {
        return BuildConfig.SDK_NAME;
    }

    /**
     * Gets sdk version for analytics.
     *
     * @return sdk version for analytics.
     */
    @NonNull
    private String getSdkVersion() {
        return BuildConfig.VERSION_NAME;
    }

    /**
     * Gets base url for Retrofit.
     *
     * @return base url for {@code Retrofit} instance.
     */
    @NonNull
    private String getBaseUrl() {
        return BuildConfig.SDK_ENDPOINT;
    }

    /**
     * Builds and returns default {@code OkHttpClient} for {@code Retrofit}.
     * If you override it, don't forget to add some interceptors.
     *
     * @return {@code OkHttpClient} instance.
     * @see SdkInterceptor
     * @see UserAgentInterceptor
     */
    private OkHttpClient buildHttpClient(Authenticator authenticator, Interceptor... interceptors) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS);
        for (Interceptor interceptor : interceptors) {
            builder.addInterceptor(interceptor);
        }
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
        return builder.addInterceptor(loggingInterceptor)
                .authenticator(authenticator)
                .build();
    }

    private Gson buildGson() {
        return new GsonBuilder()
                .setLenient()
                .registerTypeAdapter(WidgetItem.class, new WidgetItemTypeAdapter())
                .create();
    }

    /**
     * Builds and returns default {@code Retrofit} instance.
     *
     * @return {@code Retrofit} instance
     */
    private Retrofit buildRetrofit(String baseUrl, OkHttpClient httpClient, Converter.Factory converterFactory) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(converterFactory)
                .client(httpClient)
                .build();
    }

    /**
     * Returns Application Context
     *
     * @return {@code Context} instance
     */
    @NonNull
    public Context getContext() {
        return appContext;
    }

    /**
     * Returns executor for scheduled runs
     *
     * @return {@code ScheduledExecutorService} instance.
     */
    @NonNull
    public ScheduledExecutorService getExecutor() {
        return executor;
    }

    @NonNull
    public DeviceInfoProvider getDeviceInfoProvider() {
        return deviceInfoProvider;
    }

    /**
     * Returns provider for {@code AdvertisingId.Info}
     *
     * @return {@code AdvertisingIdProvider} instance
     */
    @NonNull
    public AdvertisingIdProvider getAdvertisingIdProvider() {
        return advertisingIdProvider;
    }

    @NonNull
    public UserProvider getUserProvider() {
        return userProvider;
    }

    @NonNull
    public CxenseConfiguration getCxenseConfiguration() {
        return cxenseConfiguration;
    }

    @NonNull
    public CxenseAuthenticator getAuthenticator() {
        return cxenseAuthenticator;
    }

    @NonNull
    public Gson getGson() {
        return gson;
    }

    @NonNull
    private Converter.Factory getConverterFactory() {
        return converterFactory;
    }

    @NonNull
    public ApiErrorParser getErrorParser() {
        return errorParser;
    }

    @NonNull
    public CxenseApi getApi() {
        return apiInstance;
    }

    @NonNull
    public DatabaseHelper getDatabaseHelper() {
        return databaseHelper;
    }

    @NonNull
    public EventRepository getEventRepository() {
        return eventRepository;
    }

    @NonNull
    public DispatchEventsCallback getEventsSendCallback() {
        return eventsSendCallback;
    }

    @NonNull
    public PageViewEventConverter getPageViewEventConverter() {
        return pageViewEventConverter;
    }

    @NonNull
    public PerformanceEventConverter getPerformanceEventConverter() {
        return performanceEventConverter;
    }

    @NonNull
    public SendTask getEventsSendTask() {
        return eventsSendTask;
    }

    @NonNull
    public CxenseSdk getCxenseSdk() {
        return cxenseSdk;
    }
}
