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
    public static final String FUNNEL_TYPE_CONVERT_PRODUCT = "convertProduct";
    public static final String FUNNEL_TYPE_TERMINATE_PRODUCT = "terminateProduct";
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

    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public @NonNull
    String getType() {
        return type;
    }

    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public @NonNull
    List<UserIdentity> getIdentities() {
        return identities;
    }

    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public @NonNull
    String getSiteId() {
        return siteId;
    }

    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public @NonNull
    String getProductId() {
        return productId;
    }

    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public @Nullable
    Double getPrice() {
        return price;
    }

    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public @Nullable
    String getRenewalFrequency() {
        return renewalFrequency;
    }

    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public @NonNull
    String getFunnelStep() {
        return funnelStep;
    }

    public static class Builder {
        private String eventId;
        private List<UserIdentity> identities;
        private String siteId;
        private String productId;
        private Double price;
        private String renewalFrequency;
        private String funnelStep;

        public Builder(@NonNull List<UserIdentity> identities, @NonNull String siteId, @NonNull String productId, @NonNull String funnelStep) {
            this.identities = new ArrayList<>();
            addIdentities(identities);
            setSiteId(siteId);
            setProductId(productId);
            setFunnelStep(funnelStep);
        }

        /**
         * Sets custom event id, that used for tracking locally.
         *
         * @param eventId event id
         * @return Builder instance
         */
        public Builder setEventId(@Nullable String eventId) {
            this.eventId = eventId;
            return this;
        }

        @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
        public Builder addIdentity(@NonNull UserIdentity identity) {
            Preconditions.checkForNull(identity, "identity");
            identities.add(identity);
            return this;
        }

        @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
        public Builder addIdentities(@NonNull Collection<UserIdentity> identities) {
            Preconditions.checkForNull(identities, "identities");
            for (UserIdentity identity : identities) {
                addIdentity(identity);
            }
            return this;
        }

        @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
        public Builder setSiteId(@NonNull String siteId) {
            Preconditions.checkForNull(siteId, "siteId");
            this.siteId = siteId;
            return this;
        }

        @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
        public Builder setProductId(@NonNull String productId) {
            Preconditions.checkForNull(productId, "productId");
            Preconditions.checkStringMaxLength(productId, "productId", MAX_LENGTH);
            this.productId = productId;
            return this;
        }

        @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
        public Builder setPrice(@Nullable Double price) {
            this.price = price;
            return this;
        }

        @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
        public Builder setRenewalFrequency(@Nullable String renewalFrequency) {
            Preconditions.checkStringMaxLength(renewalFrequency, "renewalFrequency", MAX_LENGTH);
            this.renewalFrequency = renewalFrequency;
            return this;
        }

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
