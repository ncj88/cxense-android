package com.cxense.cxensesdk;

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.webkit.URLUtil;

import com.cxense.ArrayFixedSizeQueue;
import com.cxense.Preconditions;
import com.cxense.cxensesdk.db.EventRecord;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Tracking page view event description.
 *
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-07-03).
 */
@SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
public final class PageViewEvent extends Event {
    public static final int MAX_EXTERNAL_USER_IDS = 5;
    /**
     * Max length for custom parameter value.
     */
    public static final int MAX_CUSTOM_PARAMETER_LENGTH = 20;
    static final String CUSTOM_PARAMETER_PREFIX = "cp_";
    static final String CUSTOM_USER_PARAMETER_PREFIX = "cp_u_";
    // Map keys constants
    static final String VERSION = "ver";
    static final String TYPE = "typ";
    static final String ACCOUNT = "acc";
    static final String SITE_ID = "sid";
    static final String LOCATION = "loc";
    static final String REFERRER = "ref";
    static final String GOAL = "gol";
    static final String PAGE_NAME = "pgn";
    static final String TIME = "ltm";
    static final String TIME_OFFSET = "tzo";
    static final String RESOLUTION = "res";
    static final String START_RESOLUTION = "wsz";
    static final String COLOR = "col";
    static final String DENSITY = "dpr";
    static final String RND = "rnd";
    static final String JAVA = "jav";
    static final String LANGUAGE = "bln";
    static final String CKP = "ckp";
    static final String ENCODING = "chs";
    static final String FLASH = "fls";
    static final String NEW_USER = "new";
    static final String LATITUDE = "plat";
    static final String LONGITUDE = "plon";
    static final String ACCURACY = "pacc";
    static final String ALTITUDE = "palt";
    static final String HEADING = "phed";
    static final String SPEED = "pspd";
    static final String ACTIVE_RND = "arnd";
    static final String ACTIVE_TIME = "altm";
    static final String ACTIVE_SPENT_TIME = "aatm";
    static final String EXTERNAL_USER_KEY = "eit";
    static final String EXTERNAL_USER_VALUE = "eid";

    static final String DEFAULT_EVENT_TYPE = "pgv";
    static final int DEFAULT_API_VERSION = 1;

    private String eventId;
    private String usi;
    /**
     * Which version of the API is this requested targeted.
     */
    private int version;
    private String type;
    private int accountId = 0;
    private String siteId;
    private String location;
    private String referrer;
    private String goalId;
    private String pageName;
    private Date date;
    private boolean isNewUser;
    private Location userLocation;
    private List<ExternalUserId> externalUserIds;
    private Map<String, String> customParameters;
    private Map<String, String> customUserParameters;

    PageViewEvent(Builder builder) {
        super();

        version = DEFAULT_API_VERSION;
        date = new Date();
        usi = CxenseSdk.getInstance().getUserId();

        eventId = builder.eventId;
        type = builder.type;
        accountId = builder.accountId;
        siteId = builder.siteId;
        location = builder.location;
        referrer = builder.referrer;
        goalId = builder.goalId;
        pageName = builder.pageName;
        isNewUser = builder.isNewUser;
        userLocation = builder.userLocation;
        externalUserIds = Collections.unmodifiableList(new ArrayList<>(builder.externalUserIds));
        customParameters = Collections.unmodifiableMap(builder.customParameters);
        customUserParameters = Collections.unmodifiableMap(builder.customUserParameters);
    }

    @Override
    public EventRecord toEventRecord() throws JsonProcessingException {
        Map<String, String> eventMap = toMap();
        EventRecord record = new EventRecord();
        record.customId = eventId;
        record.data = CxenseSdk.getInstance().packObject(eventMap);
        record.timestamp = date.getTime();
        record.ckp = eventMap.get(PageViewEvent.CKP);
        record.rnd = eventMap.get(PageViewEvent.RND);
        record.eventType = DEFAULT_EVENT_TYPE;
        return record;
    }

    /**
     * Gets custom event id, that used for tracking locally.
     *
     * @return event id
     */
    public String getEventId() {
        return eventId;
    }

    /**
     * Gets type of event.
     *
     * @return event type
     */
    public String getType() {
        return type;
    }

    /**
     * Gets the Cxense account identifier.
     *
     * @return Cxense account identifier
     */
    public int getAccountId() {
        return accountId;
    }

    /**
     * Gets the URL of the page..
     *
     * @return the page URL.
     */
    public String getLocation() {
        return location;
    }

    /**
     * Gets the URL of the referring page.
     *
     * @return referring page URL
     */
    public String getReferrer() {
        return referrer;
    }

    /**
     * Gets the Cxense goal identifier.
     *
     * @return the Cxense goal identifier.
     */
    public String getGoalId() {
        return goalId;
    }

    /**
     * Gets the page name.
     *
     * @return the page name
     */
    public String getPageName() {
        return pageName;
    }

    /**
     * Gets datetime of event.
     *
     * @return event datetime
     */
    public Date getDate() {
        return date;
    }

    /**
     * Gets flag indicates new user.
     *
     * @return true, if it is new user
     */
    public boolean isNewUser() {
        return isNewUser;
    }

    /**
     * Gets user geo location.
     *
     * @return user geo location
     */
    public Location getUserLocation() {
        return userLocation;
    }

    @NonNull
    private String escapeString(@Nullable String str) {
        return str != null ? str : "";
    }

    Map<String, String> toMap() {
        CxenseSdk cxense = CxenseSdk.getInstance();
        Calendar calendar = Calendar.getInstance();
        long offset = TimeUnit.MILLISECONDS.toMinutes(calendar.getTimeZone().getOffset(calendar.getTimeInMillis()));
        DisplayMetrics dm = CxenseSdk.getInstance().getDisplayMetrics();
        String resolution = String.format(Locale.US, "%dx%d", dm.widthPixels, dm.heightPixels);
        Locale locale = Locale.getDefault();
        String lang = String.format(Locale.US, "%s_%s", escapeString(locale.getLanguage()), escapeString(locale.getCountry()));

        Map<String, String> result = new HashMap<>();
        int i = 0;
        for (ExternalUserId userId : externalUserIds) {
            result.put("eit" + i, userId.key);
            result.put("eid" + i, userId.value);
        }
        if (cxense.getConfiguration().isAutoMetaInfoTrackingEnabled()) {
            // automatic app meta gathering
            String appName = cxense.getApplicationName();
            String appVersion = cxense.getApplicationVersion();
            if (!TextUtils.isEmpty(appName))
                result.put(CUSTOM_PARAMETER_PREFIX + "app", appName);
            if (!TextUtils.isEmpty(appVersion))
                result.put(CUSTOM_PARAMETER_PREFIX + "appv", appVersion);
        }
        result.put(SITE_ID, siteId);
        result.put(VERSION, "" + version);
        result.put(TYPE, type);
        result.put(ACCOUNT, "" + accountId);
        result.put(LOCATION, location);
        result.put(REFERRER, escapeString(referrer));
        result.put(GOAL, escapeString(goalId));
        result.put(PAGE_NAME, escapeString(pageName));
        result.put(TIME, "" + date.getTime());
        // The client's timezone.
        result.put(TIME_OFFSET, "" + offset);
        result.put(RESOLUTION, resolution);
        result.put(START_RESOLUTION, resolution);
        // Device color depth.
        result.put(COLOR, "32"); // Android uses ARGB_8888 32bit from version 2.3 (API 10)
        result.put(DENSITY, "" + dm.density);
        result.put(RND, String.format(Locale.US, "%d%d", calendar.getTimeInMillis(), (int) (Math.random() * 10E8)));
        // Is Java enabled
        result.put(JAVA, "0"); // No, we have not Java 😁
        result.put(LANGUAGE, lang);
        result.put(CKP, usi);
        result.put(ENCODING, "UTF-8");
        // Is Flash enabled?
        result.put(FLASH, "0");
        result.put(NEW_USER, isNewUser ? "1" : "0");
        for (Map.Entry<String, String> entry : customParameters.entrySet()) {
            result.put(CUSTOM_PARAMETER_PREFIX + entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, String> entry : customUserParameters.entrySet()) {
            result.put(CUSTOM_USER_PARAMETER_PREFIX + entry.getKey(), entry.getValue());
        }
        if (userLocation != null) {
            result.put(LATITUDE, "" + userLocation.getLatitude());
            result.put(LONGITUDE, "" + userLocation.getLongitude());
            if (userLocation.hasAccuracy())
                result.put(ACCURACY, "" + userLocation.getAccuracy());
            if (userLocation.hasAltitude())
                result.put(ALTITUDE, "" + userLocation.getAltitude());
            if (userLocation.hasBearing())
                result.put(HEADING, "" + userLocation.getBearing());
            if (userLocation.hasSpeed())
                result.put(SPEED, "" + userLocation.getSpeed());
        }
        return result;
    }

    /**
     * Helper class for building {@link Event}
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess", "SameParameterValue"}) // Public API.
    public static class Builder {
        private String eventId;
        private String type = DEFAULT_EVENT_TYPE;
        private int accountId = 0;
        private String siteId;
        private String location;
        private String referrer;
        private String goalId;
        private String pageName;
        private boolean isNewUser;
        private Location userLocation;
        private Map<String, String> customParameters;
        private Map<String, String> customUserParameters;
        private ArrayFixedSizeQueue<ExternalUserId> externalUserIds;

        private Builder() {
            customParameters = new HashMap<>();
            customUserParameters = new HashMap<>();
            externalUserIds = new ArrayFixedSizeQueue<>(MAX_EXTERNAL_USER_IDS);
        }

        /**
         * Initialize Builder
         *
         * @param location The URL of the page. Must be a syntactically valid URL, or else the event will be dropped.
         */
        public Builder(@NonNull String siteId, @NonNull String location) {
            this();
            setSiteId(siteId);
            setLocation(location);
        }

        /**
         * Sets custom event id, that used for tracking locally.
         *
         * @param eventId event id
         * @return Builder instance
         */
        public Builder setEventId(String eventId) {
            this.eventId = eventId;
            return this;
        }

        /**
         * Sets type of event. Default value {@link #DEFAULT_EVENT_TYPE} denotes a page-view event.
         *
         * @param type type of event
         * @return Builder instance
         */
        public Builder setType(@NonNull String type) {
            Preconditions.checkStringForNullOrEmpty(type, "type");
            this.type = type;
            return this;
        }

        /**
         * Sets the Cxense account identifier.
         *
         * @param accountId account identifier
         * @return Builder instance
         */
        public Builder setAccountId(int accountId) {
            this.accountId = accountId;
            return this;
        }

        /**
         * Sets specified site id.
         *
         * @param siteId the Cxense site identifier.
         * @return Builder instance
         */
        public Builder setSiteId(String siteId) {
            Preconditions.checkStringForNullOrEmpty(siteId, "siteId");
            this.siteId = siteId;
            return this;
        }

        /**
         * Sets the URL of the page. Must be a syntactically valid URL, or else the event will be dropped.
         *
         * @param location page URL
         * @return Builder instance
         */
        public Builder setLocation(@NonNull String location) {
            Preconditions.checkStringForNullOrEmpty(location, "location");
            this.location = URLUtil.isNetworkUrl(location) ? location : CxenseSdk.getInstance().getConfiguration().getUrlLessBaseUrl() + location;
            return this;
        }

        /**
         * Sets the URL of the referring page.
         *
         * @param referrer referring page URL
         * @return Builder instance
         */
        public Builder setReferrer(@NonNull String referrer) {
            Preconditions.checkStringForNullOrEmpty(referrer, "referrer");
            this.referrer = referrer;
            return this;
        }

        /**
         * Sets the Cxense goal identifier. For future funnelling purposes.
         *
         * @param goalId goal identifier
         * @return Builder instance
         */
        public Builder setGoalId(String goalId) {
            this.goalId = goalId;
            return this;
        }

        /**
         * Sets the page name.
         *
         * @param pageName page name
         * @return Builder instance
         */
        public Builder setPageName(String pageName) {
            this.pageName = pageName;
            return this;
        }

        /**
         * Set hint to indicate if this looks like a new user.
         *
         * @param newUser flag indicates new user
         * @return Builder instance
         */
        public Builder setNewUser(boolean newUser) {
            isNewUser = newUser;
            return this;
        }

        /**
         * Sets user geo location
         *
         * @param location User geo location
         * @return Builder instance
         */
        public Builder setUserLocation(Location location) {
            userLocation = location;
            return this;
        }

        /**
         * Add custom parameter.
         *
         * @param name  parameter name
         * @param value parameter value
         * @return Builder instance
         */
        public Builder addCustomParameter(@NonNull String name, @NonNull String value) {
            Preconditions.checkStringForNullOrEmpty(name, "name");
            Preconditions.checkStringNotNullMaxLength(value, "value", MAX_CUSTOM_PARAMETER_LENGTH);
            customParameters.put(name, value);
            return this;
        }

        /**
         * Add custom user profile parameter.
         *
         * @param name  parameter name
         * @param value parameter value
         * @return Builder instance
         */
        public Builder addCustomUserParameter(String name, @NonNull String value) {
            Preconditions.checkStringForNullOrEmpty(name, "name");
            Preconditions.checkStringNotNullMaxLength(value, "value", MAX_CUSTOM_PARAMETER_LENGTH);
            customUserParameters.put(name, value);
            return this;
        }

        /**
         * Adds external user id for this event. The external user type should be the key and the external user id the value.
         * You can add a maximum of {@link #MAX_EXTERNAL_USER_IDS} external user ids, if you add more, then last will be used.
         *
         * @param userType external user type
         * @param userId   external user id
         * @throws IllegalArgumentException if external user type has a length less than 1 or greater than 10 characters,
         *                                  if external user id has a length less than 1 or greater than 40 characters.
         */
        public Builder addExternalUserId(String userType, String userId) {
            externalUserIds.add(new ExternalUserId(userType, userId));
            return this;
        }

        /**
         * Clear external user ids list.
         */
        public Builder clearExternalUserIds() {
            externalUserIds.clear();
            return this;
        }

        /**
         * Build {@link Event} instance.
         *
         * @return Event instance
         */
        public PageViewEvent build() {
            return new PageViewEvent(this);
        }
    }
}
