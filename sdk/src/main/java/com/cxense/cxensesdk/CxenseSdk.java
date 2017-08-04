package com.cxense.cxensesdk;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;

import com.cxense.Cxense;
import com.cxense.LoadCallback;
import com.cxense.Preconditions;
import com.cxense.cxensesdk.db.DatabaseHelper;
import com.cxense.cxensesdk.db.EventRecord;
import com.cxense.cxensesdk.model.BaseUserIdentity;
import com.cxense.cxensesdk.model.CxenseUserIdentity;
import com.cxense.cxensesdk.model.EventDataRequest;
import com.cxense.cxensesdk.model.User;
import com.cxense.cxensesdk.model.UserDataRequest;
import com.cxense.cxensesdk.model.UserExternalData;
import com.cxense.cxensesdk.model.UserIdentity;
import com.cxense.cxensesdk.model.UserSegmentRequest;
import com.cxense.exceptions.CxenseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import okhttp3.Authenticator;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Singleton class used as a facade to the Cxense services
 * Read full documentation <a href="https://wiki.cxense.com/display/cust/Cxense+SDK+for+Android">here</a>
 *
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-07-03).
 */

public final class CxenseSdk extends Cxense {
    /**
     * Default "base url" for url-less mode
     */
    @SuppressWarnings("WeakerAccess") // Internal API.
    static final String DEFAULT_URL_LESS_BASE_URL = "http://%s.content.id/%s";
    private static final String TAG = CxenseSdk.class.getSimpleName();
    private static CxenseSdk instance;
    private final CxenseConfiguration configuration;
    private final DatabaseHelper databaseHelper;
    private CxenseApi apiInstance;
    private ScheduledFuture<?> scheduled;
    private SendTask sendTask;

    /**
     * @param context {@code Context} instance from {@code Activity}/{@code ContentProvider}/etc.
     */
    @SuppressWarnings("WeakerAccess") // Internal API.
    protected CxenseSdk(@NonNull Context context) {
        super(context);
        apiInstance = retrofit.create(CxenseApi.class);
        configuration = new CxenseConfiguration();
        databaseHelper = new DatabaseHelper(context);
        sendTask = new SendTask();
        initSendTaskSchedule();
    }

    public static void init(Context context) {
        instance = new CxenseSdk(context);
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

    @NonNull
    @Override
    protected String getBaseUrl() {
        return BuildConfig.SDK_ENDPOINT;
    }

    @NonNull
    @Override
    protected String getSdkName() {
        return BuildConfig.SDK_NAME;
    }

    @NonNull
    @Override
    public String getUserAgent() {
        return String.format("cx-sdk/%s %s", BuildConfig.VERSION_NAME, getDefaultUserAgent());
    }

    @Override
    protected OkHttpClient buildHttpClient() {
        return super.buildHttpClient().newBuilder().authenticator(getAuthenticator()).build();
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
        apiInstance.getUserSegments(new UserSegmentRequest(identities, siteGroupIds)).enqueue(transform(callback, data -> data.ids));
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
     * @param groups        a list of strings that specify profile item groups to keep in the returned profile. If not specified, all groups available for the user will be returned
     * @param recent        flag whether to only return the most recent user profile information. This can be used to return quickly if response time is important
     * @param identityTypes a list of external customer identifier types. If an external customer identifier exists for the user, it will be included in the response
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
        apiInstance.getUserExternalData(new BaseUserIdentity(id, type)).enqueue(transform(callback, data -> data.items));
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

    void initSendTaskSchedule() {
        if (scheduled != null)
            scheduled.cancel(false);
        scheduled = executor.scheduleWithFixedDelay(sendTask, CxenseConfiguration.DISPATCH_INITIAL_DELAY, configuration.getDispatchPeriod(), TimeUnit.MILLISECONDS);
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

    Map<String, String> unpackMap(String data) throws IOException {
        TypeReference<HashMap<String, String>> typeRef
                = new TypeReference<HashMap<String, String>>() {
        };
        return mapper.readValue(data, typeRef);
    }

    long putEventRecordInDatabase(EventRecord record) {
        return databaseHelper.save(record);
    }

    void deleteOutdatedEvents() {
        databaseHelper.delete(EventRecord.TABLE_NAME, EventRecord.TIME + " < ?", new String[]{"" + (System.currentTimeMillis() - configuration.getOutdatePeriod())});
    }

    List<EventRecord> getNotSubmittedEvents(boolean needPageViewEvents) {
        String selection = EventRecord.IS_SENT + " = 0 AND " + EventRecord.EVENT_TYPE + (needPageViewEvents ? " = ?" : " <> ?");
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

    static class SendTask implements Runnable {
        void sendDmpEvents(@NonNull List<EventRecord> events) {
            if (events.isEmpty())
                return;
            try {
                CxenseSdk cxense = CxenseSdk.getInstance();
                List<String> data = new ArrayList<>();
                for (EventRecord record : events) {
                    data.add(record.data);
                }
                Response<Void> response = cxense.apiInstance.pushEvents(new EventDataRequest(data)).execute();
                if (!response.isSuccessful())
                    return;
                for (EventRecord event : events) {
                    event.isSent = true;
                    cxense.putEventRecordInDatabase(event);
                }
            } catch (IOException e) {
                // TODO: May be we need to rethrow new exception?
                Log.e(TAG, "Can't push dmp events data", e);
            }
        }

        void sendPageViewEvents(@NonNull List<EventRecord> events) {
            CxenseSdk cxense = CxenseSdk.getInstance();
            for (EventRecord event : events) {
                try {
                    Response<ResponseBody> response = cxense.apiInstance.track(cxense.unpackMap(event.data)).execute();
                    if (response.isSuccessful()) {
                        event.isSent = true;
                        cxense.putEventRecordInDatabase(event);
                    }
                } catch (IOException e) {
                    // TODO: May be we need to rethrow new exception?
                    Log.e(TAG, "Can't deserialize event data", e);
                }
            }
        }

        @Override
        public void run() {
            try {
                CxenseSdk cxense = CxenseSdk.getInstance();
                cxense.deleteOutdatedEvents();
                if (cxense.configuration.getDispatchMode() == CxenseConfiguration.DispatchMode.OFFLINE || cxense.configuration.isRestricted(cxense.appContext))
                    return;

                sendDmpEvents(cxense.getNotSubmittedEvents(false));
                sendPageViewEvents(cxense.getNotSubmittedEvents(true));

            } catch (Exception e) {
                Log.e(TAG, "Error at sending data", e);
            }
        }
    }
}
