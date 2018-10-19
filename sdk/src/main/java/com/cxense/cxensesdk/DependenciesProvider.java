package com.cxense.cxensesdk;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.cxense.cxensesdk.db.DatabaseHelper;
import com.cxense.cxensesdk.model.EventRepository;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

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
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2018-09-17).
 */
public final class DependenciesProvider {
    private static final String TAG = DependenciesProvider.class.getSimpleName();

    private static DependenciesProvider instance;
    private final Context appContext;
    private final String defaultUserAgent;
    private final ScheduledExecutorService executor;
    private final DeviceInfoProvider deviceInfoProvider;
    private final AdvertisingIdProvider advertisingIdProvider;
    private final UserProvider userProvider;
    private final CxenseConfiguration cxenseConfiguration;
    private final CxenseAuthenticator cxenseAuthenticator;
    private final OkHttpClient okHttpClient;
    private final ObjectMapper mapper;
    private final Converter.Factory converterFactory;
    private final Retrofit retrofit;
    private final ApiErrorParser errorParser;
    private final CxenseApi apiInstance;
    private final DatabaseHelper databaseHelper;
    private final EventRepository eventRepository;
    private final DispatchEventsCallback eventsSendCallback;
    private final PageViewEventConverter pageViewEventConverter;
    private final PerformanceEventConverter performanceEventConverter;
    private final SendTask eventsSendTask;
    private final CxenseSdk cxenseSdk;


    private DependenciesProvider(Context context) {
        appContext = context.getApplicationContext();
        defaultUserAgent = getDefaultUserAgent(appContext);
        executor = Executors.newSingleThreadScheduledExecutor();
        deviceInfoProvider = new DeviceInfoProvider(appContext);
        advertisingIdProvider = new AdvertisingIdProvider(appContext, executor);
        userProvider = new UserProvider(advertisingIdProvider);
        cxenseConfiguration = new CxenseConfiguration();
        cxenseAuthenticator = new CxenseAuthenticator(cxenseConfiguration);
        Interceptor sdkInterceptor = new SdkInterceptor(getSdkName(), getSdkVersion()),
                userAgentInterceptor = new UserAgentInterceptor(getUserAgent());
        okHttpClient = buildHttpClient(cxenseAuthenticator, sdkInterceptor, userAgentInterceptor);
        mapper = buildMapper();
        pageViewEventConverter = new PageViewEventConverter(mapper, cxenseConfiguration, deviceInfoProvider);
        performanceEventConverter = new PerformanceEventConverter(mapper, cxenseConfiguration);
        converterFactory = JacksonConverterFactory.create(mapper);
        retrofit = buildRetrofit(getBaseUrl(), okHttpClient, converterFactory);
        Converter<ResponseBody, ApiError> errorConverter = retrofit.responseBodyConverter(ApiError.class, new Annotation[0]);
        errorParser = new ApiErrorParser(errorConverter);
        apiInstance = retrofit.create(CxenseApi.class);
        databaseHelper = new DatabaseHelper(appContext);
        eventRepository = new EventRepository(databaseHelper, mapper, Arrays.asList(pageViewEventConverter, performanceEventConverter));
        eventsSendCallback = statuses -> {
            for (EventStatus eventStatus : statuses) {
                if (eventStatus.exception != null) {
                    Log.e("CxenseEventCallback", String.format(Locale.getDefault(), "Error at sending event with id '%s'",
                            eventStatus.eventId), eventStatus.exception);
                }
            }
        };
        eventsSendTask = new SendTask(apiInstance, eventRepository, cxenseConfiguration, deviceInfoProvider, userProvider, mapper, performanceEventConverter, errorParser, eventsSendCallback);
        cxenseSdk = new CxenseSdk(executor, cxenseConfiguration, advertisingIdProvider, userProvider, apiInstance, errorParser, mapper, eventRepository, eventsSendTask);
    }

    public static void init(Context context) {
        instance = new DependenciesProvider(context);
    }

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
     * Gets default user-agent from Android
     *
     * @return system default user-agent
     */
    private String getDefaultUserAgent(Context context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                return WebSettings.getDefaultUserAgent(context);
            }
            return new WebView(context).getSettings().getUserAgentString();
        } catch (Exception e) {
            /*
            This block is needed as attempt to avoid problem with Android System WebView
            service's update during which any requests to WebViews will be finished
            with android.content.pm.PackageManager$NameNotFoundException.

            What is important here, that 'user-agent' is required param in Cxense Insight API,
            so, we need to provide it. We can use 'http.agent' property's value here, but
            it provides less details about device than WebView. That is why property's value
            is used without defaultUserAgent field's initialization.

            Best practise here - always using WebView's 'user-agent' string.

            Bug in Android issue tracker can be found here:
            https://code.google.com/p/android/issues/detail?id=175124

            Good explanation of the problem can be found here:
            https://bugs.chromium.org/p/chromium/issues/detail?id=506369
             */
            Log.e(TAG, e.getMessage(), e);
        }
        return System.getProperty("http.agent", "");
    }

    /**
     * Returns the user-agent used by the SDK
     */
    @NonNull
    private String getUserAgent() {
        return String.format("cx-sdk/%s %s", BuildConfig.VERSION_NAME, defaultUserAgent);
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

    /**
     * Builds and returns default object mapper for Jackson. You may override it in descendant.
     *
     * @return {@link ObjectMapper} instance
     */
    private ObjectMapper buildMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper;
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
    public Context getContext() {
        return appContext;
    }

    /**
     * Returns executor for scheduled runs
     *
     * @return {@code ScheduledExecutorService} instance.
     */
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
    public ObjectMapper getMapper() {
        return mapper;
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
