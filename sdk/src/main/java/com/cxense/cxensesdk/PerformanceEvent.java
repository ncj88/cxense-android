package com.cxense.cxensesdk;

import android.support.annotation.NonNull;

import com.cxense.Preconditions;
import com.cxense.cxensesdk.db.EventRecord;
import com.cxense.cxensesdk.model.CustomParameter;
import com.cxense.cxensesdk.model.UserIdentity;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Push event description
 *
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-05).
 */

public final class PerformanceEvent extends Event {
    @JsonProperty("time")
    private Long time;
    @JsonProperty("userIds")
    private List<UserIdentity> identities;
    @JsonProperty("prnd")
    private String prnd;
    @JsonProperty("rnd")
    private String rnd;
    @JsonProperty("siteId")
    private String siteId;
    @JsonProperty("origin")
    private String origin;
    @JsonProperty("type")
    private String type;
    @JsonProperty("segmentIds")
    private List<String> segments;
    @JsonProperty("customParameters")
    private List<CustomParameter> customParameters;

    private PerformanceEvent(Builder builder) {
        super();
        time = builder.time;
        identities = Collections.unmodifiableList(builder.identities);
        prnd = builder.prnd;
        rnd = builder.rnd;
        siteId = builder.siteId;
        origin = builder.origin;
        type = builder.type;
        segments = Collections.unmodifiableList(builder.segments);
        customParameters = Collections.unmodifiableList(builder.customParameters);
    }

    @Override
    public EventRecord toEventRecord() throws JsonProcessingException {
        EventRecord record = new EventRecord();
        record.data = CxenseSdk.getInstance().packObject(this);
        // event time in seconds, but timestamp in milliseconds
        record.timestamp = time != null ? TimeUnit.SECONDS.toMillis(time): System.currentTimeMillis();
        record.ckp = prnd;
        record.rnd = rnd;
        record.eventType = type;
        return record;
    }

    /**
     * Gets the exact datetime of an event
     *
     * @return datetime of an event
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public Date getTime() {
        return time != null ? new Date(time) : null;
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
        Long time;
        List<UserIdentity> identities;
        String prnd;
        String rnd;
        String siteId;
        String origin;
        String type;
        List<String> segments;
        List<CustomParameter> customParameters;

        /**
         * Initialize Builder with required parameters
         *
         * @param identities List of known user identities to identify the user. Note that different users must be fed as different events.
         * @param siteId     The analytics site identifier to be associated with the events.
         * @param origin     Differentiates various DMP applications used by the customer. Must be prefixed by the customer prefix.
         * @param type       Differentiates various event types, e.g., "click", "impression", "conversion", etc.
         */
        public Builder(@NonNull Collection<UserIdentity> identities, @NonNull String siteId, @NonNull String origin, @NonNull String type) {
            this.identities = new ArrayList<>();
            this.segments = new ArrayList<>();
            this.customParameters = new ArrayList<>();
            addIdentities(identities);
            setSiteId(siteId);
            setOrigin(origin);
            setType(type);
        }

        private void setTime(long milliseconds) {
            this.time = TimeUnit.MILLISECONDS.toSeconds(milliseconds);
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
         * In order to link DMP events to page views this value must be identical to the rnd value of the page view event.
         *
         * @param prnd A value uniquely identifying the page request
         * @return Builder instance
         */
        public Builder setPrnd(String prnd) {
            this.prnd = prnd;
            return this;
        }

        /**
         * Sets an alternative specification for eventId.
         * Multiple events on the same page view must have distinct rnd values.
         *
         * @param rnd A value uniquely identifying the action.
         * @return Builder instance
         */
        public Builder setRnd(String rnd) {
            this.rnd = rnd;
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
            Preconditions.checkStringForRegex(origin, "origin", "\\w{3}-\\w+", "'%s' must be prefixed by the customer prefix.", "origin");
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
            return new PerformanceEvent(this);
        }
    }
}
