package com.cxense.cxensesdk.model;


import androidx.annotation.NonNull;

import com.cxense.cxensesdk.DependenciesProvider;
import com.cxense.cxensesdk.Preconditions;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Push event description
 *
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-05).
 */

public final class PerformanceEvent extends Event {
    public static final String TIME = "time";
    public static final String USER_IDS = "userIds";
    public static final String PRND = "prnd";
    public static final String RND = "rnd";
    public static final String SITE_ID = "siteId";
    public static final String ORIGIN = "origin";
    public static final String TYPE = "type";
    public static final String SEGMENT_IDS = "segmentIds";
    public static final String CUSTOM_PARAMETERS = "customParameters";
    @JsonProperty(TIME)
    private Long time;
    @JsonProperty(USER_IDS)
    private List<UserIdentity> identities;
    @JsonProperty(PRND)
    private String prnd;
    @JsonProperty(RND)
    private String rnd;
    @JsonProperty(SITE_ID)
    private String siteId;
    @JsonProperty(ORIGIN)
    private String origin;
    @JsonProperty(TYPE)
    private String type;
    @JsonProperty(SEGMENT_IDS)
    private List<String> segments;
    @JsonProperty(CUSTOM_PARAMETERS)
    private List<CustomParameter> customParameters;
    @JsonProperty("consent")
    private List<String> consentOptions;

    private PerformanceEvent() {
        super(null);
    }

    private PerformanceEvent(Builder builder, List<String> consentOptions) {
        super(builder.eventId);
        rnd = String.format(Locale.US, "%d%d", Calendar.getInstance().getTimeInMillis(), (int) (Math.random() * 10E8));

        time = builder.time;
        identities = Collections.unmodifiableList(builder.identities);
        prnd = builder.prnd;
        siteId = builder.siteId;
        origin = builder.origin;
        type = builder.type;
        segments = Collections.unmodifiableList(builder.segments);
        customParameters = Collections.unmodifiableList(builder.customParameters);
        this.consentOptions = consentOptions;
    }

    /**
     * Gets the exact datetime of an event
     *
     * @return datetime of an event
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public Date getTime() {
        return time != null ? new Date(TimeUnit.SECONDS.toMillis(time)) : null;
    }

    /**
     * Gets list of known user identities to identify the user.
     *
     * @return user identities
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public List<UserIdentity> getIdentities() {
        return identities;
    }

    /**
     * Gets an alternative specification for page view event id.
     *
     * @return prnd
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public String getPrnd() {
        return prnd;
    }

    /**
     * Gets an alternative specification for eventId.
     *
     * @return rnd
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public String getRnd() {
        return rnd;
    }

    /**
     * Gets the analytics site identifier to be associated with the events.
     *
     * @return site identifier
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public String getSiteId() {
        return siteId;
    }

    /**
     * Gets origin, that various DMP applications used by the customer.
     *
     * @return origin
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public String getOrigin() {
        return origin;
    }

    /**
     * Gets event type, e.g., "click", "impression", "conversion", etc.
     *
     * @return event type
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public String getType() {
        return type;
    }

    /**
     * Gets optional collection of matching segments to be reported.
     *
     * @return matching segments
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public List<String> getSegments() {
        return segments;
    }

    /**
     * Gets optional collection of customer-defined parameters to event.
     *
     * @return customer-defined parameters
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public List<CustomParameter> getCustomParameters() {
        return customParameters;
    }

    /**
     * Helper class for building {@link PerformanceEvent}
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess, UnusedReturnValue"}) // Public API.
    public static class Builder {
        private String eventId;
        private Long time;
        private List<UserIdentity> identities;
        private String prnd;
        private String siteId;
        private String origin;
        private String type;
        private List<String> segments;
        private List<CustomParameter> customParameters;

        /**
         * Initialize Builder with required parameters
         *
         * @param identities List of known user identities to identify the user. Note that different users must be fed
         *                   as different events.
         * @param siteId     The analytics site identifier to be associated with the events.
         * @param origin     Differentiates various DMP applications used by the customer. Must be prefixed by the
         *                   customer prefix.
         * @param type       Differentiates various event types, e.g., "click", "impression", "conversion", etc.
         */
        public Builder(@NonNull Collection<UserIdentity> identities, @NonNull String siteId, @NonNull String origin,
                       @NonNull String type) {
            this.identities = new ArrayList<>();
            this.segments = new ArrayList<>();
            this.customParameters = new ArrayList<>();
            addIdentities(identities);
            setSiteId(siteId);
            setOrigin(origin);
            setType(type);
        }

        void setTime(long milliseconds) {
            this.time = TimeUnit.MILLISECONDS.toSeconds(milliseconds);
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
         * Sets the exact datetime of an event
         *
         * @param date datetime object
         * @return Builder instance
         */
        public Builder setTime(Date date) {
            setTime(date.getTime());
            return this;
        }

        /**
         * Sets current datetime as exact datetime of an event
         *
         * @return Builder instance
         */
        public Builder setCurrentTime() {
            setTime(System.currentTimeMillis());
            return this;
        }

        /**
         * Adds known user identity to identify the user.
         *
         * @param identity {@link UserIdentity} instance
         * @return Builder instance
         */
        public Builder addIdentity(@NonNull UserIdentity identity) {
            Preconditions.checkForNull(identity, "identity");
            identities.add(identity);
            return this;
        }

        /**
         * Adds collection of known user identities to identify the user.
         *
         * @param identities {@link Collection} of {@link UserIdentity} objects
         * @return Builder instance
         */
        public Builder addIdentities(@NonNull Collection<UserIdentity> identities) {
            Preconditions.checkForNull(identities, "identities");
            for (UserIdentity identity : identities) {
                this.addIdentity(identity);
            }
            return this;
        }

        /**
         * Sets an alternative specification for page view event id.
         * In order to link DMP events to page views this value must be identical to the rnd value of the page view
         * event.
         *
         * @param prnd A value uniquely identifying the page request
         * @return Builder instance
         */
        public Builder setPrnd(String prnd) {
            this.prnd = prnd;
            return this;
        }

        /**
         * Sets the analytics site identifier to be associated with the events.
         *
         * @param siteId site identifier
         * @return Builder instance
         */
        public Builder setSiteId(@NonNull String siteId) {
            Preconditions.checkStringForNullOrEmpty(siteId, "siteId");
            this.siteId = siteId;
            return this;
        }

        /**
         * Differentiates various DMP applications used by the customer. Must be prefixed by the customer prefix.
         *
         * @param origin prefixed by the customer prefix value
         * @return Builder instance
         */
        public Builder setOrigin(String origin) {
            Preconditions.checkStringForRegex(origin, "origin", "\\w{3}-[\\w-]+", "'%s' must be " +
                    "prefixed by the customer prefix.", "origin");
            this.origin = origin;
            return this;
        }

        /**
         * Differentiates various event types, e.g., "click", "impression", "conversion", etc.
         *
         * @param type event type
         * @return Builder instance
         */
        public Builder setType(String type) {
            Preconditions.checkStringForNullOrEmpty(type, "type");
            this.type = type;
            return this;
        }

        /**
         * Adds optional collection of matching segments to be reported.
         *
         * @param segments matching segments
         * @return Builder instance
         */
        public Builder addSegments(Collection<String> segments) {
            this.segments.addAll(segments);
            return this;
        }

        /**
         * Adds customer-defined parameter to event.
         *
         * @param parameter {@link CustomParameter} object
         * @return Builder instance
         */
        public Builder addCustomParameter(CustomParameter parameter) {
            this.customParameters.add(parameter);
            return this;
        }

        /**
         * Adds optional collection of customer-defined parameters to event.
         *
         * @param parameters {@link Collection} of {@link CustomParameter} objects
         * @return Builder instance
         */
        public Builder addCustomParameters(Collection<CustomParameter> parameters) {
            this.customParameters.addAll(parameters);
            return this;
        }

        /**
         * Build {@link PerformanceEvent} instance.
         *
         * @return PerformanceEvent instance
         */
        public PerformanceEvent build() {
            return new PerformanceEvent(this, DependenciesProvider.getInstance().getCxenseConfiguration().getConsentOptionsValues());
        }
    }
}
