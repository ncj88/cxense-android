package com.cxense.cxensesdk.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cxense.cxensesdk.DependenciesProvider;
import com.cxense.cxensesdk.Preconditions;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Conversion event description
 *
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2019-04-03).
 */
public class ConversionEvent extends Event {
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public static final String FUNNEL_TYPE_CONVERT_PRODUCT = "convertProduct";
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public static final String FUNNEL_TYPE_TERMINATE_PRODUCT = "terminateProduct";
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public static final String FUNNEL_TYPE_RENEW_PRODUCT = "renewProduct";
    static final String EVENT_TYPE = "conversion";
    static final int MAX_LENGTH = 30;

    @JsonProperty("eventType")
    private String type = EVENT_TYPE;
    @JsonProperty("userIds")
    private List<UserIdentity> identities;
    @JsonProperty("siteId")
    private String siteId;
    @JsonProperty("consent")
    private List<String> consentOptions;

    @JsonProperty("productId")
    private String productId;
    @JsonProperty("productPrice")
    private Double price;
    @JsonProperty("productRenewalFrequency")
    private String renewalFrequency;
    @JsonProperty("funnelStep")
    private String funnelStep;

    private ConversionEvent() {
        super(null);
    }

    private ConversionEvent(Builder builder, List<String> consentOptions) {
        this();
        identities = Collections.unmodifiableList(builder.identities);
        siteId = builder.siteId;
        productId = builder.productId;
        price = builder.price;
        renewalFrequency = builder.renewalFrequency;
        funnelStep = builder.funnelStep;
        this.consentOptions = consentOptions;
    }

    /**
     * Gets type of event.
     *
     * @return event type
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    @NonNull
    public String getType() {
        return type;
    }

    /**
     * Gets user identities
     *
     * @return user identities
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    @NonNull
    public List<UserIdentity> getIdentities() {
        return identities;
    }

    /**
     * Gets the Cxense site identifier.
     *
     * @return the Cxense site identifier.
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    @NonNull
    public String getSiteId() {
        return siteId;
    }

    /**
     * Gets the conversion product id.
     *
     * @return the identifier of the conversion product object the conversion is associated with.
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    @NonNull
    public String getProductId() {
        return productId;
    }

    /**
     * Gets new product price.
     *
     * @return the price to override the original value in the conversion product object.
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    @Nullable
    public Double getPrice() {
        return price;
    }

    /**
     * Gets new product renewal frequency.
     *
     * @return the renewal frequency to override the original value in the conversion product object.
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    @Nullable
    public String getRenewalFrequency() {
        return renewalFrequency;
    }

    /**
     * Gets current funnel step.
     *
     * @return the current step in the conversion funnel the event represents.
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    @NonNull
    public String getFunnelStep() {
        return funnelStep;
    }

    public static class Builder {
        private List<UserIdentity> identities;
        private String siteId;
        private String productId;
        private Double price;
        private String renewalFrequency;
        private String funnelStep;

        /**
         * Initialize Builder with required parameters
         *
         * @param identities List of known user identities to identify the user. Note that different users must be fed
         *                   as different events.
         * @param siteId     The Cxense site identifier to be associated with the events.
         * @param productId  product identifier
         * @param funnelStep funnel step
         **/
        public Builder(@NonNull List<UserIdentity> identities, @NonNull String siteId, @NonNull String productId, @NonNull String funnelStep) {
            this.identities = new ArrayList<>();
            addIdentities(identities);
            setSiteId(siteId);
            setProductId(productId);
            setFunnelStep(funnelStep);
        }

        /**
         * Adds known user identity to identify the user.
         *
         * @param identity {@link UserIdentity} instance
         * @return Builder instance
         */
        @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
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
        @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
        public Builder addIdentities(@NonNull Collection<UserIdentity> identities) {
            Preconditions.checkForNull(identities, "identities");
            for (UserIdentity identity : identities) {
                addIdentity(identity);
            }
            return this;
        }

        /**
         * Sets Cxense site identifier.
         *
         * @param siteId Cxense site identifier.
         * @return Builder instance
         */
        @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
        public Builder setSiteId(@NonNull String siteId) {
            Preconditions.checkStringForNullOrEmpty(siteId, "siteId");
            this.siteId = siteId;
            return this;
        }

        /**
         * Sets an identifier of the conversion product object the conversion is associated with.
         *
         * @param productId product identifier
         * @return Builder instance
         */
        @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
        public Builder setProductId(@NonNull String productId) {
            Preconditions.checkForNull(productId, "productId");
            Preconditions.checkStringMaxLength(productId, "productId", MAX_LENGTH);
            this.productId = productId;
            return this;
        }

        /**
         * Sets a price to override the original value in the conversion product object.
         *
         * @param price new price value
         * @return Builder instance
         */
        @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
        public Builder setPrice(@Nullable Double price) {
            this.price = price;
            return this;
        }

        /**
         * Sets a renewal frequency to override the original value in the conversion product object.
         * The renewal frequency has the format "<number><units><type>". If the renewal frequency is set on the product, the system will automatically renew all the conversions to this product every <number> of <units> until the conversion is explicitly stopped, renewed or started over.
         * The <number> is limited to 3 digits. Only 'd' (days), 'w' (weeks), 'M' (months) and 'y' (years) are supported as <units>. The <type> can be one of 'R' (relative to the time the user has converted) or 'C' (calendar-based: happening at the beginning of the <unit>).
         * Examples: "1yC", "28wR" and so on.
         *
         * @param renewalFrequency new renewal frequency value.
         * @return Builder instance
         */
        @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
        public Builder setRenewalFrequency(@Nullable String renewalFrequency) {
            if (renewalFrequency != null)
                Preconditions.checkStringForRegex(renewalFrequency, "renewalFrequency", "^\\d{1,3}[dwMy][CR]$", "The renewal frequency has the format \"<number><units><type>\". The <number> is limited to 3 digits. Only 'd' (days), 'w' (weeks), 'M' (months) and 'y' (years) are supported as <units>. The <type> can be one of 'R' (relative to the time the user has converted) or 'C' (calendar-based: happening at the beginning of the <unit>).");
            this.renewalFrequency = renewalFrequency;
            return this;
        }

        /**
         * Sets current step in the conversion funnel the event represents. Can be one of the pre-defined {@link #FUNNEL_TYPE_CONVERT_PRODUCT}, {@link #FUNNEL_TYPE_TERMINATE_PRODUCT} and {@link #FUNNEL_TYPE_RENEW_PRODUCT} or alternatively any string representing the step e.g. 'creditCardDetails'.
         *
         * @param funnelStep new funnel step
         * @return Builder instance
         */
        @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
        public Builder setFunnelStep(@NonNull String funnelStep) {
            Preconditions.checkForNull(funnelStep, "funnelStep");
            Preconditions.checkStringMaxLength(funnelStep, "funnelStep", MAX_LENGTH);
            this.funnelStep = funnelStep;
            return this;
        }

        public ConversionEvent build() {
            return new ConversionEvent(this, DependenciesProvider.getInstance().getCxenseConfiguration().getConsentOptionsValues());
        }
    }
}
