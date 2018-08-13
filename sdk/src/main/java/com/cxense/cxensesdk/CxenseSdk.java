package com.cxense.cxensesdk;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.cxense.cxensesdk.db.DatabaseHelper;
import com.cxense.cxensesdk.db.EventRecord;
import com.cxense.cxensesdk.exceptions.BadRequestException;
import com.cxense.cxensesdk.exceptions.CxenseException;
import com.cxense.cxensesdk.exceptions.ForbiddenException;
import com.cxense.cxensesdk.exceptions.NotAuthorizedException;
import com.cxense.cxensesdk.model.BaseUserIdentity;
import com.cxense.cxensesdk.model.ContentUser;
import com.cxense.cxensesdk.model.CxenseUserIdentity;
import com.cxense.cxensesdk.model.EventDataRequest;
import com.cxense.cxensesdk.model.User;
import com.cxense.cxensesdk.model.UserDataRequest;
import com.cxense.cxensesdk.model.UserExternalData;
import com.cxense.cxensesdk.model.UserIdentity;
import com.cxense.cxensesdk.model.UserSegmentRequest;
import com.cxense.cxensesdk.model.WidgetItem;
import com.cxense.cxensesdk.model.WidgetRequest;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import okhttp3.Authenticator;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;


/**
 * Singleton class used as a facade to the Cxense services
 * Read full documentation <a href="https://wiki.cxense.com/display/cust/Cxense+SDK+for+Android">here</a>
 *
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-07-03).
 */

public final class CxenseSdk {
    /**
     * Default "base url" for url-less mode
     */
    @SuppressWarnings("WeakerAccess") // Internal API.
    static final String DEFAULT_URL_LESS_BASE_URL = "http://%s.content.id/%s";
    private static final String TAG = CxenseSdk.class.getSimpleName();
    private static CxenseSdk instance;
    private final CxenseConfiguration configuration;
    private final DatabaseHelper databaseHelper;
    SendTask sendTask;
    private CxenseApi apiInstance;
    private ScheduledFuture<?> scheduled;
    private ContentUser defaultUser;
    private DispatchEventsCallback sendCallback = statuses -> {
        for (EventStatus eventStatus : statuses) {
            if (eventStatus.exception != null) {
                Log.e(TAG, String.format(Locale.getDefault(), "Error at sending event with id '%s'",
                        eventStatus.eventId), eventStatus.exception);
            }
        }
    };
    private static final long DELAY = 300;
    /**
     * Current App Context for SDK
     */
    @SuppressWarnings("WeakerAccess") // Internal API.
    protected final Context appContext;
    /**
     * ScheduledExecutorService instance
     */
    @SuppressWarnings("WeakerAccess") // Internal API.
    protected final ScheduledExecutorService executor;
    private final Runnable getAdvertisingInfoTask;
    /**
     * Default Object <-> JSON Mapper
     */
    protected ObjectMapper mapper;
    /**
     * OkHttpClient instance
     *
     * @see #buildHttpClient()
     */
    @SuppressWarnings("WeakerAccess") // Internal API.
    protected OkHttpClient okHttpClient;
    /**
     * Retrofit instance.
     *
     * @see #buildRetrofit()
     */
    @SuppressWarnings("WeakerAccess") // Internal API.
    protected Retrofit retrofit;
    /**
     * User advertising information from Google Play Services.
     */
    @SuppressWarnings("WeakerAccess") // Internal API.
    protected AdvertisingIdClient.Info advertisingInfo;
    @SuppressWarnings("WeakerAccess") // Internal API.
    protected String userId;
    private Set<ConsentOption> consentOptions = new HashSet<>();

    /**
     * @param context {@code Context} instance from {@code Activity}/{@code ContentProvider}/etc.
     */
    protected CxenseSdk(@NonNull Context context) {
        appContext = context.getApplicationContext();
        mapper = buildMapper();
        okHttpClient = buildHttpClient();
        retrofit = buildRetrofit();
        executor = buildExecutor();
        getAdvertisingInfoTask = () -> {
            try {
                advertisingInfo = AdvertisingIdClient.getAdvertisingIdInfo(appContext);
                userId = getDefaultUserId();
            } catch (IOException | GooglePlayServicesRepairableException e) {
                Log.e(TAG, e.getMessage(), e);
                initAdvertisingIdTask();
            } catch (GooglePlayServicesNotAvailableException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        };
        initAdvertisingIdTask();

        apiInstance = retrofit.create(CxenseApi.class);
        configuration = new CxenseConfiguration();
        databaseHelper = new DatabaseHelper(context);
        sendTask = new SendTask();
        initSendTaskSchedule();
    }

    @SuppressWarnings("WeakerAccess") // Internal API.
    protected static void throwIfUninitialized(CxenseSdk instance) {
        if (instance == null)
            throw new IllegalStateException("The Cxense SDK instance is not initialized! Make " +
                    "sure to call init before calling other methods.");
    }

    static void init(Context context) {
        instance = new CxenseSdk(context);
    }

    /**
     * Gets default user-agent from Android
     *
     * @return system default user-agent
     */
    @SuppressWarnings("WeakerAccess") // Internal API.
    protected String getDefaultUserAgent() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                return WebSettings.getDefaultUserAgent(appContext);
            }
            return new WebView(appContext).getSettings().getUserAgentString();
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
     * Builds and returns default object mapper for Jackson. You may override it in descendant.
     *
     * @return {@link ObjectMapper} instance
     */
    @SuppressWarnings("WeakerAccess") // Internal API.
    protected ObjectMapper buildMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper;
    }

    /**
     * Builds and returns default converter factory (Jackson) for {@code Retrofit}. You may override it in descendant.
     *
     * @return converter factory for {@code Retrofit}
     */
    @SuppressWarnings("WeakerAccess") // Internal API.
    @NonNull
    protected Converter.Factory getConverterFactory() {
        return JacksonConverterFactory.create(mapper);
    }

    /**
     * Builds and returns default {@code OkHttpClient} for {@code Retrofit}.
     * If you override it, don't forget to add some interceptors.
     *
     * @return {@code OkHttpClient} instance.
     * @see SdkInterceptor
     * @see UserAgentInterceptor
     */
    @SuppressWarnings("WeakerAccess") // Internal API.
    protected OkHttpClient buildHttpClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
        return new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(new SdkInterceptor(getSdkName(), getSdkVersion()))
                .addInterceptor(new UserAgentInterceptor(getUserAgent()))
                .addInterceptor(interceptor)
                .authenticator(getAuthenticator())
                .build();
    }

    /**
     * Builds and returns default {@code Retrofit} instance.
     * It uses {@link #getBaseUrl()}, {@link #getConverterFactory()}, {@link #buildHttpClient()} for building.
     *
     * @return {@code Retrofit} instance
     */
    @SuppressWarnings("WeakerAccess") // Internal API.
    protected Retrofit buildRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(getBaseUrl())
                .addConverterFactory(getConverterFactory())
                .client(okHttpClient)
                .build();
    }

    /**
     * Creates and returns {@code ScheduledExecutorService} instance.
     *
     * @return {@code ScheduledExecutorService} instance.
     */
    @SuppressWarnings("WeakerAccess") // Internal API.
    protected ScheduledExecutorService buildExecutor() {
        return Executors.newSingleThreadScheduledExecutor();
    }

    /**
     * Transforms {@link LoadCallback} to {@link Callback}
     *
     * @param callback LoadCallback instance
     * @param <T>      Successful response type.
     * @return Callback instance
     */
    @SuppressWarnings("WeakerAccess") // Internal API.
    protected <T> Callback<T> transform(final LoadCallback<T> callback) {
        return new ApiCallback<>(callback, this);
    }

    /**
     * Transforms {@link LoadCallback} to {@link Callback} using {@link Function}
     *
     * @param callback LoadCallback instance
     * @param function Function for apply from T to U
     * @param <T>      Successful response type.
     * @param <U>      Callback type.
     * @return Callback instance
     */
    @SuppressWarnings("WeakerAccess") // Internal API.
    protected <T, U> Callback<T> transform(final LoadCallback<U> callback, final Function<T, U> function) {
        return transform(new LoadCallback<T>() {
            @Override
            public void onSuccess(T data) {
                if (callback != null)
                    callback.onSuccess(function.apply(data));
            }

            @Override
            public void onError(Throwable throwable) {
                if (callback != null)
                    callback.onError(throwable);
            }
        });
    }

    /**
     * Parse {@link Response} to {@link CxenseException} object
     *
     * @param response Retrofit response
     * @return CxenseException instance, if response is unsuccessful, else null
     */
    @SuppressWarnings("WeakerAccess") // Internal API.
    @Nullable
    protected CxenseException parseError(@NonNull Response<?> response) {
        if (response.isSuccessful())
            return null;
        try {
            Converter<ResponseBody, ApiError> converter = retrofit.responseBodyConverter(ApiError.class,
                    new Annotation[0]);
            ApiError apiError;
            try {
                apiError = converter.convert(response.errorBody());
            } catch (IOException ex) {
                apiError = new ApiError();
            }
            String message = apiError.error != null ? apiError.error : "";
            switch (response.code()) {
                case 400:
                    return new BadRequestException(message);
                case 401:
                    return new NotAuthorizedException(message);
                case 403:
                    return new ForbiddenException(message);
                default:
                    return new CxenseException(message);
            }
        } catch (Exception e) {
            return new CxenseException(e.getMessage());
        }
    }

    /**
     * Retrieves the user id used by this SDK.
     *
     * @return current user id
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the user id used by this SDK. Must be at least 16 characters long.
     * Allowed characters are: A-Z, a-z, 0-9, "_", "-", "+" and ".".
     *
     * @param id new user id
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public void setUserId(@NonNull String id) {
        Preconditions.checkStringForRegex(id, "id", "^[\\w-+.]{16,}$",
                "The user id must be at least 16 characters long. Allowed characters are: " +
                        "A-Z, a-z, 0-9, \"_\", \"-\", \"+\" and \".\".");
        userId = id;
    }

    /**
     * Retrieves the default user id for SDK.
     *
     * @return advertising ID (if available)
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    @Nullable
    public String getDefaultUserId() {
        return advertisingInfo != null ? advertisingInfo.getId() : null;
    }

    /**
     * Retrieves whether the user has limit ad tracking enabled or not.
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public boolean isLimitAdTrackingEnabled() {
        return advertisingInfo != null && advertisingInfo.isLimitAdTrackingEnabled();
    }

    /**
     * Returns current consent options for user
     *
     * @return current consent options
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public Set<ConsentOption> getConsentOptions() {
        return Collections.unmodifiableSet(consentOptions);
    }

    /**
     * Set consent options for user data
     *
     * @param options new options
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public void setConsentOptions(ConsentOption... options) {
        consentOptions = new HashSet<>(Arrays.asList(options));
    }

    /**
     * Returns current consent options for user as string values
     *
     * @return current consent options string values
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public List<String> getConsentOptionsValues() {
        List<String> values = new ArrayList<>();
        for (ConsentOption option : consentOptions) {
            values.add(option.getValue());
        }
        return values;
    }

    /**
     * Returns current consent options for user as comma-delimited string
     *
     * @return comma-delimited string with current consent options
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public String getConsentOptionsAsString() {
        if (consentOptions.isEmpty())
            return null;
        return TextUtils.join(",", getConsentOptionsValues());
    }

    void postRunnable(Runnable runnable) {
        executor.execute(runnable);
    }

    private void initAdvertisingIdTask() {
        executor.schedule(getAdvertisingInfoTask, DELAY, TimeUnit.MILLISECONDS);
    }

    /**
     * Gets singleton SDK instance.
     *
     * @return singleton SDK instance.
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public static CxenseSdk getInstance() {
        throwIfUninitialized(instance);
        return instance;
    }

    /**
     * Creates a new widget with the specified widget id
     *
     * @param widgetId the widget id
     * @return the new widget
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public static Widget createWidget(@NonNull String widgetId) {
        if (TextUtils.isEmpty(widgetId))
            throw new IllegalArgumentException("widgetId can't be empty.");
        return new Widget(widgetId);
    }

    /**
     * Tracks an url click for the given item
     *
     * @param item the item that contains the click-url
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public static void trackClick(@NonNull WidgetItem item) {
        trackClick(item.clickUrl);
    }

    /**
     * Tracks a click for the given click-url
     *
     * @param url the click-url
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public static void trackClick(@NonNull String url) {
        CxenseSdk.getInstance().apiInstance.trackUrlClick(url).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable throwable) {
            }
        });
    }

    /**
     * Gets base url for Retrofit. You must override it in descendant.
     *
     * @return base url for {@code Retrofit} instance.
     */
    @NonNull
    String getBaseUrl() {
        return BuildConfig.SDK_ENDPOINT;
    }

    /**
     * Gets sdk name for analytics. You must override it in descendant.
     *
     * @return sdk name for analytics.
     */
    @NonNull
    String getSdkName() {
        return BuildConfig.SDK_NAME;
    }

    /**
     * Gets sdk version for analytics. You must override it in descendant.
     *
     * @return sdk version for analytics.
     */
    @NonNull
    private String getSdkVersion() {
        return BuildConfig.VERSION_NAME;
    }

    /**
     * Returns the user-agent used by the SDK
     */
    @NonNull
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public String getUserAgent() {
        return String.format("cx-sdk/%s %s", BuildConfig.VERSION_NAME, getDefaultUserAgent());
    }

    @SuppressWarnings("WeakerAccess")
        // Internal API.
    Authenticator getAuthenticator() {
        return new CxenseAuthenticator();
    }

    void updateAuth(@NonNull String username, @NonNull String apiKey) {
        CxenseAuthenticator authenticator = (CxenseAuthenticator) okHttpClient.authenticator();
        if (authenticator == null) {
            Log.d(TAG, "Authenticator is not available");
            return;
        }
        authenticator.updateCredentials(username, apiKey);
    }

    /**
     * Gets Cxense SDK configuration
     *
     * @return sdk configuration
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public CxenseConfiguration getConfiguration() {
        return configuration;
    }

    /**
     * Asynchronously retrieves a list of all segments where the specified user is a member
     *
     * @param identities   a list of user identifiers for a single user to retrieve segments for
     * @param siteGroupIds the list of site groups to retrieve segments for
     * @param callback     a  callback to receive a list of segment identifiers where the specified user is a member
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public void getUserSegmentIds(@NonNull List<UserIdentity> identities,
                                  @NonNull List<String> siteGroupIds,
                                  final LoadCallback<List<String>> callback) throws CxenseException {
        Preconditions.checkForNull(identities, "identities");
        Preconditions.checkForNull(siteGroupIds, "siteGroupIds");
        Set<ConsentOption> consentOptions = getConsentOptions();
        if (consentOptions.contains(ConsentOption.CONSENT_REQUIRED) && !consentOptions.contains(ConsentOption.SEGMENT_ALLOWED)) {
            callback.onSuccess(Collections.emptyList());
            return;
        }
        apiInstance.getUserSegments(new UserSegmentRequest(identities, siteGroupIds))
                .enqueue(transform(callback, data -> data.ids));
    }

    /**
     * Asynchronously retrieves a suitably authorized slice of a given user's interest profile
     *
     * @param identity user identifier with type and id
     * @param callback a callback with user profile
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public void getUser(@NonNull UserIdentity identity,
                        LoadCallback<User> callback) throws CxenseException {
        getUser(identity, null, null, null, callback);
    }

    /**
     * Asynchronously retrieves a suitably authorized slice of a given user's interest profile
     *
     * @param identity      user identifier with type and id
     * @param groups        a list of strings that specify profile item groups to keep in the returned profile.
     *                      If not specified, all groups available for the user will be returned
     * @param recent        flag whether to only return the most recent user profile information. This can be used to
     *                      return quickly if response time is important
     * @param identityTypes a list of external customer identifier types. If an external customer identifier exists for
     *                      the user, it will be included in the response
     * @param callback      a callback with {@link User} profile
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess", "SameParameterValue"}) // Public API.
    public void getUser(@NonNull UserIdentity identity,
                        List<String> groups,
                        Boolean recent,
                        List<String> identityTypes,
                        LoadCallback<User> callback) throws CxenseException {
        Preconditions.checkForNull(identity, "identity");
        apiInstance.getUser(new UserDataRequest(identity, groups, recent, identityTypes)).enqueue(transform(callback));
    }

    /**
     * Asynchronously retrieves the external data associated with a given user type
     *
     * @param type     the customer identifier type
     * @param callback a callback with {@link UserExternalData}
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public void getUserExternalData(@NonNull String type,
                                    LoadCallback<List<UserExternalData>> callback)
            throws CxenseException {
        getUserExternalData(null, type, callback);
    }

    /**
     * Asynchronously retrieves the external data associated with a given user
     *
     * @param id       identifier for the user. Use 'null' if you want match all users of provided type.
     * @param type     the customer identifier type
     * @param callback a callback with {@link UserExternalData}
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess", "SameParameterValue"}) // Public API.
    public void getUserExternalData(String id,
                                    @NonNull String type,
                                    LoadCallback<List<UserExternalData>> callback)
            throws CxenseException {
        apiInstance.getUserExternalData(new BaseUserIdentity(id, type))
                .enqueue(transform(callback, data -> data.items));
    }

    /**
     * Asynchronously sets the external data associated with a given user
     *
     * @param userExternalData external data associated with a user
     * @param callback         a callback
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess", "SameParameterValue"}) // Public API.
    public void setUserExternalData(@NonNull UserExternalData userExternalData,
                                    LoadCallback<Void> callback) throws CxenseException {
        Preconditions.checkForNull(userExternalData, "userExternalData");
        apiInstance.updateUserExternalData(userExternalData).enqueue(transform(callback));
    }

    /**
     * Asynchronously deletes the external data associated with a given user
     *
     * @param identity user identifier with type and id
     * @param callback a callback
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public void deleteUserExternalData(@NonNull UserIdentity identity,
                                       LoadCallback<Void> callback) throws CxenseException {
        Preconditions.checkForNull(identity, "identity");
        apiInstance.deleteExternalUserData(identity).enqueue(transform(callback));
    }

    /**
     * Asynchronously retrieves a registered external identity mapping for a Cxense identifier
     *
     * @param cxenseId the Cxense identifier of the user.
     * @param type     the identity mapping type (customer identifier type) that contains the mapping.
     * @param callback a callback with {@link UserIdentity}
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public void getUserExternalLink(@NonNull String cxenseId,
                                    @NonNull String type,
                                    LoadCallback<UserIdentity> callback) throws CxenseException {
        apiInstance.getUserExternalLink(new CxenseUserIdentity(cxenseId, type)).enqueue(transform(callback));
    }

    /**
     * Asynchronously register a new identity-mapping for the given user
     *
     * @param cxenseId the Cxense identifier to map this user to
     * @param identity user identifier with type and id
     * @param callback a callback with {@link UserIdentity}
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public void setUserExternalLink(@NonNull String cxenseId,
                                    @NonNull UserIdentity identity,
                                    LoadCallback<UserIdentity> callback) throws CxenseException {
        apiInstance.updateUserExternalLink(new CxenseUserIdentity(identity, cxenseId)).enqueue(transform(callback));
    }

    void putEvents(final Event... events) {
        for (Event event : events) {
            try {
                putEventRecordInDatabase(event.toEventRecord());
            } catch (JsonProcessingException e) {
                // TODO: May be we need to rethrow new exception?
                Log.e(TAG, "Can't serialize event data", e);
            } catch (Exception e) {
                Log.e(TAG, "Error at pushing event", e);
            }
        }
    }

    void putEventTime(String eventId, long activeTime) {
        try {
            EventRecord record = getEventFromDatabase(eventId);
            // Only for page view events
            if (record == null)
                return;
            EventRecord newRecord = new EventRecord(record);
            // some black magic with map
            if (activeTime == 0)
                activeTime = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - newRecord.timestamp);
            newRecord.spentTime = activeTime;

            Map<String, String> eventMap = unpackMap(newRecord.data);
            eventMap.put(PageViewEvent.ACTIVE_RND, eventMap.get(PageViewEvent.RND));
            eventMap.put(PageViewEvent.ACTIVE_TIME, eventMap.get(PageViewEvent.TIME));
            eventMap.put(PageViewEvent.ACTIVE_SPENT_TIME, "" + activeTime);
            newRecord.data = packObject(eventMap);

            putEventRecordInDatabase(newRecord);
        } catch (JsonProcessingException e) {
            Log.e(TAG, "Can't serialize event data", e);
        } catch (IOException e) {
            // TODO: May be we need to rethrow new exception?
            Log.e(TAG, "Can't deserialize event data", e);
        } catch (Exception e) {
            Log.e(TAG, "Error at tracking time", e);
        }
    }

    /**
     * Push events to sending queue.
     *
     * @param events the events that should be pushed.
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public void pushEvents(@NonNull Event... events) {
        postRunnable(() -> putEvents(events));
    }

    /**
     * Tracks active time for the given page view event. The active time will be calculated
     * as the time between this call and the trackEvent call.
     *
     * @param eventId the event to report active time for.
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public void trackActiveTime(String eventId) {
        trackActiveTime(eventId, 0);
    }

    /**
     * Tracks active time for the given page view event.
     *
     * @param eventId    the event to report active time for.
     * @param activeTime the active time in seconds.
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess", "SameParameterValue"}) // Public API.
    public void trackActiveTime(final String eventId, final long activeTime) {
        postRunnable(() -> putEventTime(eventId, activeTime));
    }

    /**
     * Returns the default user used by all widgets if the user hasn't been specifically set on a widget
     *
     * @return the default user
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public ContentUser getDefaultUser() {
        if (defaultUser == null) {
            defaultUser = new ContentUser(getUserId());
        }
        return defaultUser;
    }

    /**
     * Forces sending events from queue to server.
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public void flushEventQueue() {
        sendTask.run();
    }

    /**
     * Returns current event queue status
     *
     * @return queue status
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    @NonNull
    public QueueStatus getQueueStatus() {
        List<ContentValues> values = databaseHelper.query(EventRecord.TABLE_NAME,
                new String[]{EventRecord.EVENT_CUSTOM_ID, EventRecord.IS_SENT}, null,
                null, null, null, EventRecord.TIME + " ASC");
        List<EventStatus> statuses = new ArrayList<>();
        for (ContentValues cv : values) {
            EventRecord record = new EventRecord(cv);
            statuses.add(new EventStatus(record.customId, record.isSent));
        }
        return new QueueStatus(statuses);
    }

    /**
     * Sets callback for each dispatching of events
     *
     * @param callback callback instance
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public void setDispatchEventsCallback(DispatchEventsCallback callback) {
        sendCallback = callback;
    }

    private <T> Callback<ResponseBody> createGenericCallback(LoadCallback<T> callback) {
        return transform(new LoadCallback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody responseBody) {
                try {
                    Class<T> clazz = (Class<T>) ((ParameterizedType) callback.getClass().getGenericInterfaces()[0])
                            .getActualTypeArguments()[0];
                    callback.onSuccess(mapper.readValue(responseBody.charStream(), clazz));
                } catch (Exception e) {
                    callback.onError(e);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                callback.onError(throwable);
            }
        });
    }

    /**
     * Executes persisted query to Cxense API endpoint. You can find some popular endpoints in {@link CxenseConstants}
     *
     * @param url               API endpoint
     * @param persistentQueryId query id
     * @param callback          callback for response data
     * @param <T>               response type
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public <T> void executePersistedQuery(String url, String persistentQueryId, LoadCallback<T> callback) {
        apiInstance.getPersisted(url, persistentQueryId).enqueue(createGenericCallback(callback));
    }

    /**
     * Executes persisted query to Cxense API endpoint. You can find some popular endpoints in {@link CxenseConstants}
     *
     * @param url               API endpoint
     * @param persistentQueryId query id
     * @param data              data for sending as request body
     * @param callback          callback for response data
     * @param <T>               response type
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public <T> void executePersistedQuery(String url, String persistentQueryId, Object data, LoadCallback<T> callback) {
        apiInstance.postPersisted(url, persistentQueryId, data).enqueue(createGenericCallback(callback));
    }

    void getWidgetItems(WidgetRequest request, LoadCallback<List<WidgetItem>> listener) {
        apiInstance.getWidgetData(request).enqueue(transform(listener, data -> data.items));
    }

    void initSendTaskSchedule() {
        if (scheduled != null)
            scheduled.cancel(false);
        scheduled = executor.scheduleWithFixedDelay(sendTask, CxenseConfiguration.DISPATCH_INITIAL_DELAY,
                configuration.getDispatchPeriod(), TimeUnit.MILLISECONDS);
    }

    DisplayMetrics getDisplayMetrics() {
        return appContext.getResources().getDisplayMetrics();
    }

    /**
     * Get version of the application in which SDK is used.
     *
     * @return application's version or 'null' if not found
     */
    @Nullable
    String getApplicationVersion() {
        try {
            return appContext.getPackageManager().getPackageInfo(appContext.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Problems during application version search", e);
        }
        return null;
    }

    /**
     * Get name of the application in which SDK is used.
     *
     * @return application's name or 'null' if not found
     */
    @Nullable
    String getApplicationName() {
        CharSequence label = appContext.getPackageManager().getApplicationLabel(appContext.getApplicationInfo());
        return label != null ? label.toString() : null;
    }

    String packObject(Object data) throws JsonProcessingException {
        return mapper.writeValueAsString(data);
    }

    <T> T unpackObject(String data, TypeReference<T> typeReference) throws IOException {
        return mapper.readValue(data, typeReference);
    }

    Map<String, String> unpackMap(String data) throws IOException {
        return unpackObject(data, new TypeReference<HashMap<String, String>>() {
        });
    }

    long putEventRecordInDatabase(EventRecord record) {
        return databaseHelper.save(record);
    }

    void deleteOutdatedEvents() {
        databaseHelper.delete(EventRecord.TABLE_NAME, EventRecord.TIME + " < ?",
                new String[]{"" + (System.currentTimeMillis() - configuration.getOutdatePeriod())});
    }

    List<EventRecord> getNotSubmittedEvents(boolean needPageViewEvents) {
        String selection = EventRecord.IS_SENT + " = 0 AND " + EventRecord.EVENT_TYPE
                + (needPageViewEvents ? " = ?" : " <> ?");
        List<ContentValues> values = databaseHelper.query(EventRecord.TABLE_NAME, EventRecord.COLUMNS,
                selection, new String[]{PageViewEvent.DEFAULT_EVENT_TYPE}, null, null, EventRecord.TIME + " ASC");
        List<EventRecord> records = new ArrayList<>();
        for (ContentValues cv : values) {
            records.add(new EventRecord(cv));
        }
        return records;
    }

    EventRecord getEventFromDatabase(String eventId) {
        List<ContentValues> values = databaseHelper.query(EventRecord.TABLE_NAME, EventRecord.COLUMNS,
                EventRecord.EVENT_CUSTOM_ID + "= ? AND " + EventRecord.EVENT_TYPE + "= ?",
                new String[]{eventId, PageViewEvent.DEFAULT_EVENT_TYPE}, null,
                null, EventRecord.TIME + " DESC");
        if (values.isEmpty())
            return null;
        return new EventRecord(values.get(0));
    }

    public interface DispatchEventsCallback {
        void onSend(List<EventStatus> statuses);
    }

    static class SendTask implements Runnable {
        private EventStatus createStatus(EventRecord record, Exception exc) {
            return new EventStatus(record.customId, record.isSent, exc);
        }

        void sendDmpEvents(@NonNull List<EventRecord> events) {
            if (events.isEmpty())
                return;
            CxenseSdk cxense = CxenseSdk.getInstance();
            List<EventStatus> statuses = new ArrayList<>();
            if (cxense.configuration.isDmpAuthorized()) {
                Exception exception = null;
                try {
                    List<String> data = new ArrayList<>();
                    for (EventRecord record : events) {
                        data.add(record.data);
                    }
                    Response<Void> response = cxense.apiInstance.pushEvents(new EventDataRequest(data)).execute();
                    if (response.isSuccessful()) {
                        for (EventRecord event : events) {
                            event.isSent = true;
                            cxense.putEventRecordInDatabase(event);
                        }
                    }
                    exception = cxense.parseError(response);
                } catch (IOException e) {
                    exception = e;
                } finally {
                    for (EventRecord event : events) {
                        statuses.add(new EventStatus(event.customId, event.isSent, exception));
                    }
                }
            } else {
                for (EventRecord event : events) {
                    EventStatus status = null;
                    try {
                        Map<String, String> data = cxense.unpackObject(event.data,
                                new TypeReference<PerformanceEvent>() {
                                }).toQueryMap();
                        String segmentsValue = data.get(PerformanceEvent.SEGMENT_IDS);
                        data.remove(PerformanceEvent.SEGMENT_IDS);
                        List<String> segments = new ArrayList<>();
                        if (!TextUtils.isEmpty(segmentsValue)) {
                            segments.addAll(Arrays.asList(segmentsValue.split(",")));
                        }
                        Response<ResponseBody> response = cxense.apiInstance.trackDmpEvent(
                                cxense.configuration.getDmpPushPersistentId(), segments, data
                        ).execute();
                        if (response.isSuccessful()) {
                            event.isSent = true;
                        }
                        cxense.putEventRecordInDatabase(event);
                        status = createStatus(event, cxense.parseError(response));
                    } catch (IOException e) {
                        status = createStatus(event, e);
                    } finally {
                        statuses.add(status);
                    }
                }
            }
            if (cxense.sendCallback != null)
                cxense.sendCallback.onSend(statuses);
        }

        void sendPageViewEvents(@NonNull List<EventRecord> events) {
            CxenseSdk cxense = CxenseSdk.getInstance();
            List<EventStatus> statuses = new ArrayList<>();
            for (EventRecord event : events) {
                EventStatus status = null;
                try {
                    Map<String, String> data = cxense.unpackMap(event.data);
                    String ckp = data.get(PageViewEvent.CKP);
                    String id = cxense.getUserId();
                    if (TextUtils.isEmpty(ckp) && !TextUtils.isEmpty(id)) {
                        data.put(PageViewEvent.CKP, id);
                        event.data = cxense.packObject(data);
                        event.ckp = id;
                    }
                    Response<ResponseBody> response = cxense.apiInstance.trackInsightEvent(data).execute();
                    if (response.isSuccessful()) {
                        event.isSent = true;
                    }
                    cxense.putEventRecordInDatabase(event);
                    status = createStatus(event, cxense.parseError(response));
                } catch (IOException e) {
                    status = createStatus(event, e);
                } finally {
                    statuses.add(status);
                }
            }
            if (cxense.sendCallback != null)
                cxense.sendCallback.onSend(statuses);
        }

        @Override
        public void run() {
            try {
                CxenseSdk cxense = CxenseSdk.getInstance();
                cxense.deleteOutdatedEvents();
                if (cxense.configuration.getDispatchMode() == CxenseConfiguration.DispatchMode.OFFLINE
                        || cxense.configuration.isRestricted(cxense.appContext))
                    return;

                Set<ConsentOption> consentOptions = cxense.getConsentOptions();
                if (consentOptions.contains(ConsentOption.CONSENT_REQUIRED) && !consentOptions.contains(ConsentOption.PV_ALLOWED))
                    return;
                sendPageViewEvents(cxense.getNotSubmittedEvents(true));
                sendDmpEvents(cxense.getNotSubmittedEvents(false));

            } catch (Exception e) {
                Log.e(TAG, "Error at sending data", e);
            }
        }
    }
}
