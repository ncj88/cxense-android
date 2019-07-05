package com.cxense.cxensesdk.model;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cxense.cxensesdk.ArrayFixedSizeQueue;
import com.cxense.cxensesdk.DependenciesProvider;
import com.cxense.cxensesdk.Preconditions;
import com.cxense.cxensesdk.UserProvider;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Tracking page view event description.
 * Page view event has support for two modes: standard page view event and URL-less mode for content view event
 *
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-07-03).
 */
@SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
public final class PageViewEvent extends Event {
    public static final int MAX_EXTERNAL_USER_IDS = 5;
    /**
     * Max length for custom parameter value.
     */
    public static final int MAX_CUSTOM_PARAMETER_KEY_LENGTH = 20;
    public static final int MAX_CUSTOM_PARAMETER_VALUE_LENGTH = 256;
    public static final String DEFAULT_EVENT_TYPE = "pgv";

    private String ckp;
    private String type;
    private String rnd;
    private int accountId;
    private String siteId;
    private String contentId;
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

    PageViewEvent(Builder builder, String userId) {
        super(builder.eventId);

        date = new Date();
        ckp = userId;
        rnd = String.format(Locale.US, "%d%d", Calendar.getInstance().getTimeInMillis(), (int) (Math.random() * 10E8));

        type = builder.type;
        accountId = builder.accountId;
        siteId = builder.siteId;
        contentId = builder.contentId;
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

    /**
     * Gets type of event.
     *
     * @return event type
     */
    public String getType() {
        return type;
    }

    /**
     * Gets Ckp of event
     *
     * @return Ckp
     */
    public String getCkp() {
        return ckp;
    }

    /**
     * Gets Rnd of event
     *
     * @return Rnd
     */
    public String getRnd() {
        return rnd;
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
     * Gets the Cxense site identifier.
     *
     * @return the Cxense site identifier.
     */
    public String getSiteId() {
        return siteId;
    }

    /**
     * Gets content id for URL-less mode.
     *
     * @return content id
     */
    public String getContentId() {
        return contentId;
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

    /**
     * Gets user external ids
     *
     * @return user external ids
     */
    public List<ExternalUserId> getExternalUserIds() {
        return externalUserIds;
    }

    /**
     * Gets custom parameters
     *
     * @return custom parameters
     */
    public Map<String, String> getCustomParameters() {
        return customParameters;
    }

    /**
     * Gets custom user parameters
     *
     * @return custom user parameters
     */
    public Map<String, String> getCustomUserParameters() {
        return customUserParameters;
    }

    /**
     * Helper class for building {@link Event}
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess", "SameParameterValue"}) // Public API.
    public static class Builder {
        private final UserProvider userProvider;
        private String eventId;
        private String type = DEFAULT_EVENT_TYPE;
        private int accountId = 0;
        private String siteId;
        private String contentId;
        private String location;
        private String referrer;
        private String goalId;
        private String pageName;
        private boolean isNewUser;
        private Location userLocation;
        private Map<String, String> customParameters = new HashMap<>();
        private Map<String, String> customUserParameters = new HashMap<>();
        private ArrayFixedSizeQueue<ExternalUserId> externalUserIds = new ArrayFixedSizeQueue<>(MAX_EXTERNAL_USER_IDS);

        Builder(UserProvider userProvider) {
            this.userProvider = userProvider;
        }

        /**
         * Initialize Builder
         *
         * @param siteId the Cxense site identifier.
         */
        public Builder(@NonNull String siteId) {
            this(DependenciesProvider.getInstance().getUserProvider());
            setSiteId(siteId);
        }

        /**
         * Sets custom event id, that used for tracking locally.
         *
         * @param eventId event id
         * @return Builder instance
         */
        @NonNull
        public Builder setEventId(@Nullable String eventId) {
            this.eventId = eventId;
            return this;
        }

        /**
         * Sets type of event. Default value {@link #DEFAULT_EVENT_TYPE} denotes a page-view event.
         *
         * @param type type of event
         * @return Builder instance
         */
        @NonNull
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
        @NonNull
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
        @NonNull
        public Builder setSiteId(@NonNull String siteId) {
            Preconditions.checkStringForNullOrEmpty(siteId, "siteId");
            this.siteId = siteId;
            return this;
        }

        /**
         * Sets content id for URL-less mode.
         * Forces to ignore page location value.
         *
         * @param contentId content id
         * @return Builder instance
         */
        @NonNull
        public Builder setContentId(@NonNull String contentId) {
            Preconditions.checkStringForNullOrEmpty(contentId, "contentId");
            this.contentId = contentId;
            return this;
        }

        /**
         * Sets the URL of the page. Must be a syntactically valid URL, or else the event will be dropped.
         * This value will be ignored, if you setup content id.
         *
         * @param location page URL
         * @return Builder instance
         */
        @NonNull
        public Builder setLocation(@NonNull String location) {
            Preconditions.checkStringIsUrl(location, "location", "location must be valid URL");
            this.location = location;
            return this;
        }

        /**
         * Sets the URL of the referring page. Must be a syntactically valid URL
         *
         * @param referrer referring page URL
         * @return Builder instance
         */
        @NonNull
        public Builder setReferrer(@NonNull String referrer) {
            Preconditions.checkStringIsUrl(referrer, "referrer", "referrer must be valid URL");
            this.referrer = referrer;
            return this;
        }

        /**
         * Sets the Cxense goal identifier. For future funnelling purposes.
         *
         * @param goalId goal identifier
         * @return Builder instance
         */
        @NonNull
        public Builder setGoalId(@Nullable String goalId) {
            this.goalId = goalId;
            return this;
        }

        /**
         * Sets the page name.
         *
         * @param pageName page name
         * @return Builder instance
         */
        @NonNull
        public Builder setPageName(@Nullable String pageName) {
            this.pageName = pageName;
            return this;
        }

        /**
         * Set hint to indicate if this looks like a new user.
         *
         * @param newUser flag indicates new user
         * @return Builder instance
         */
        @NonNull
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
        @NonNull
        public Builder setUserLocation(@Nullable Location location) {
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
        @NonNull
        public Builder addCustomParameter(@NonNull String name, @NonNull String value) {
            Preconditions.checkStringNotNullMaxLength(name, "name", MAX_CUSTOM_PARAMETER_KEY_LENGTH);
            Preconditions.checkStringNotNullMaxLength(value, "value", MAX_CUSTOM_PARAMETER_VALUE_LENGTH);
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
        @NonNull
        public Builder addCustomUserParameter(@NonNull String name, @NonNull String value) {
            Preconditions.checkStringNotNullMaxLength(name, "name", MAX_CUSTOM_PARAMETER_KEY_LENGTH);
            Preconditions.checkStringNotNullMaxLength(value, "value", MAX_CUSTOM_PARAMETER_VALUE_LENGTH);
            customUserParameters.put(name, value);
            return this;
        }

        /**
         * Adds external user id for this event. The external user type should be the key and the external user id the
         * value.
         * You can add a maximum of {@link #MAX_EXTERNAL_USER_IDS} external user ids, if you add more, then last will be
         * used.
         *
         * @param userType external user type
         * @param userId   external user id
         * @return Builder instance
         * @throws IllegalArgumentException if external user type has a length less than 1 or greater than 10
         *                                  characters or if external user id has a length less than 1 or greater than
         *                                  40 characters.
         */
        @NonNull
        public Builder addExternalUserId(@NonNull String userType, @NonNull String userId) {
            return addExternalUserIds(new ExternalUserId(userType, userId));
        }

        /**
         * Adds external user ids for this event.
         * You can add a maximum of {@link #MAX_EXTERNAL_USER_IDS} external user ids, if you add more, then last will be
         * used.
         *
         * @param userIds User ids
         * @return Builder instance
         * @since 1.6.1
         */
        @NonNull
        public Builder addExternalUserIds(@NonNull ExternalUserId... userIds) {
            for (ExternalUserId userId : userIds) {
                Preconditions.checkForNull(userId, "userId");
                externalUserIds.add(userId);
            }
            return this;
        }

        /**
         * Clear external user ids list.
         */
        @NonNull
        public Builder clearExternalUserIds() {
            externalUserIds.clear();
            return this;
        }

        /**
         * Build {@link Event} instance.
         *
         * @return Event instance
         * @throws IllegalStateException if location and content id does not specified.
         */
        @NonNull
        public PageViewEvent build() {
            if (location == null && contentId == null)
                throw new IllegalStateException("You should specify page location or content id");
            return new PageViewEvent(this, userProvider.getUserId());
        }
    }
}
