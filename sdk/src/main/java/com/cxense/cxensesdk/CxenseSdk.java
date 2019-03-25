package com.cxense.cxensesdk;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cxense.cxensesdk.exceptions.CxenseException;
import com.cxense.cxensesdk.model.BaseUserIdentity;
import com.cxense.cxensesdk.model.ContentUser;
import com.cxense.cxensesdk.model.CxenseUserIdentity;
import com.cxense.cxensesdk.model.Event;
import com.cxense.cxensesdk.model.EventRepository;
import com.cxense.cxensesdk.model.User;
import com.cxense.cxensesdk.model.UserDataRequest;
import com.cxense.cxensesdk.model.UserExternalData;
import com.cxense.cxensesdk.model.UserIdentity;
import com.cxense.cxensesdk.model.UserSegmentRequest;
import com.cxense.cxensesdk.model.WidgetContext;
import com.cxense.cxensesdk.model.WidgetItem;
import com.cxense.cxensesdk.model.WidgetRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.reflect.ParameterizedType;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Singleton class used as a facade to the Cxense services
 * Read full documentation <a href="https://wiki.cxense.com/display/cust/Cxense+SDK+for+Android">here</a>
 *
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-07-03).
 */

public final class CxenseSdk {
    private final CxenseConfiguration configuration;
    private final EventRepository eventRepository;
    private final ScheduledExecutorService executor;
    private final AdvertisingIdProvider advertisingIdProvider;
    private final UserProvider userProvider;
    private final CxenseApi apiInstance;
    private final ApiErrorParser errorParser;
    private final ObjectMapper mapper;
    private final SendTask sendTask;

    private ScheduledFuture<?> scheduled;

    /**
     */
    CxenseSdk(@NonNull ScheduledExecutorService executor, @NonNull CxenseConfiguration cxenseConfiguration,
              @NonNull AdvertisingIdProvider advertisingIdProvider, UserProvider userProvider, @NonNull CxenseApi cxenseApi,
              @NonNull ApiErrorParser errorParser, @NonNull ObjectMapper objectMapper, @NonNull EventRepository eventRepository,
              @NonNull SendTask sendTask) {
        this.executor = executor;
        configuration = cxenseConfiguration;
        this.advertisingIdProvider = advertisingIdProvider;
        this.userProvider = userProvider;
        apiInstance = cxenseApi;
        this.errorParser = errorParser;
        mapper = objectMapper;
        this.eventRepository = eventRepository;
        this.sendTask = sendTask;

        configuration.setDispatchPeriodListener(millis -> initSendTaskSchedule());
        initSendTaskSchedule();
    }

    /**
     * Gets singleton SDK instance.
     *
     * @return singleton SDK instance.
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public static CxenseSdk getInstance() {
        return DependenciesProvider.getInstance().getCxenseSdk();
    }

    /**
     * Tracks an url click for the given item
     *
     * @param item the item that contains the click-url
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public void trackClick(@NonNull WidgetItem item) {
        trackClick(item.clickUrl);
    }

    /**
     * Tracks a click for the given click-url
     *
     * @param url the click-url
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public void trackClick(@NonNull String url) {
        apiInstance.trackUrlClick(url).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable throwable) {
            }
        });
    }

    /**
     * Fetch async a list of {@link com.cxense.cxensesdk.model.WidgetItem items} for the given {@link WidgetContext} and widget id
     *
     * @param widgetId      the widget id
     * @param widgetContext the WidgetContext
     * @param listener      listener for returning result
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public void loadWidgetRecommendations(final String widgetId, final WidgetContext widgetContext, final LoadCallback<List<WidgetItem>> listener) {
        loadWidgetRecommendations(widgetId, widgetContext, null, listener);
    }

    /**
     * @param widgetId      the widget id
     * @param widgetContext the WidgetContext
     * @param user          custom user
     * @param listener      listener for returning result
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public void loadWidgetRecommendations(final String widgetId, final WidgetContext widgetContext, ContentUser user,
                                          final LoadCallback<List<WidgetItem>> listener) {
        Preconditions.checkStringForNullOrEmpty(widgetId, "widgetId");
        if (user == null)
            user = getDefaultUser();
        apiInstance.getWidgetData(new WidgetRequest(widgetId, widgetContext, user, configuration.getConsentOptionsValues())).enqueue(transform(listener, data -> data.items));
    }

    /**
     * Transforms {@link LoadCallback} to {@link Callback}
     *
     * @param callback LoadCallback instance
     * @param <T>      Successful response type.
     * @return Callback instance
     */
    <T> Callback<T> transform(final LoadCallback<T> callback) {
        return new ApiCallback<>(callback, errorParser);
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
    <T, U> Callback<T> transform(final LoadCallback<U> callback, final Function<T, U> function) {
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
     * Retrieves the user id used by this SDK.
     *
     * @return current user id
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public String getUserId() {
        return userProvider.getUserId();
    }

    /**
     * Sets the user id used by this SDK. Must be at least 16 characters long.
     * Allowed characters are: A-Z, a-z, 0-9, "_", "-", "+" and ".".
     *
     * @param id new user id
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public void setUserId(@NonNull String id) {
        userProvider.setUserId(id);
    }

    /**
     * Retrieves the default user id for SDK.
     *
     * @return advertising ID (if available)
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    @Nullable
    public String getDefaultUserId() {
        return advertisingIdProvider.getDefaultUserId();
    }

    /**
     * Retrieves whether the user has limit ad tracking enabled or not.
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public boolean isLimitAdTrackingEnabled() {
        return advertisingIdProvider.isLimitAdTrackingEnabled();
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
        Set<ConsentOption> consentOptions = configuration.getConsentOptions();
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

    void putEventTime(String eventId, long activeTime) {
        eventRepository.putEventTime(eventId, activeTime);
    }

    /**
     * Push events to sending queue.
     *
     * @param events the events that should be pushed.
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public void pushEvents(@NonNull Event... events) {
        executor.execute(() -> eventRepository.putEventsInDatabase(events));
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
        executor.execute(() -> putEventTime(eventId, activeTime));
    }

    /**
     * Returns the default user used by all widgets if the user hasn't been specifically set on a widget
     *
     * @return the default user
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public ContentUser getDefaultUser() {
        return userProvider.getContentUser();
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
        return new QueueStatus(eventRepository.getEventStatuses());
    }

    /**
     * Sets callback for each dispatching of events
     *
     * @param callback callback instance
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public void setDispatchEventsCallback(DispatchEventsCallback callback) {
        sendTask.setDispatchEventsCallback(callback);
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

    private void initSendTaskSchedule() {
        if (scheduled != null)
            scheduled.cancel(false);
        scheduled = executor.scheduleWithFixedDelay(sendTask, CxenseConfiguration.DISPATCH_INITIAL_DELAY,
                configuration.getDispatchPeriod(), TimeUnit.MILLISECONDS);
    }
}
