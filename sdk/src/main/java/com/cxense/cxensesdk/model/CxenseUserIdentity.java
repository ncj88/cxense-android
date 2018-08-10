package com.cxense.cxensesdk.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.cxense.cxensesdk.Preconditions;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Cxense user identity object.
 *
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-05).
 */
@SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
public final class CxenseUserIdentity extends UserIdentity {
    @JsonProperty("cxenseId")
    private String cxenseId;

    /**
     * @param identity User identity object
     * @param cxenseId The Cxense-specific user identifier, either an internal cross-site user id
     */
    public CxenseUserIdentity(@NonNull UserIdentity identity, @Nullable String cxenseId) {
        super(identity);
        setCxenseId(cxenseId);
    }

    /**
     * @param type     The customer-specific identifier type as registered with Cxense
     * @param cxenseId The Cxense-specific user identifier, either an internal cross-site user id
     */
    public CxenseUserIdentity(@Nullable String type, @Nullable String cxenseId) {
        super(type);
        setCxenseId(cxenseId);
    }

    /**
     * Gets the Cxense-specific user identifier, either an internal cross-site user id
     *
     * @return Cxense-specific user identifier
     */
    public String getCxenseId() {
        return cxenseId;
    }

    /**
     * Sets the Cxense-specific user identifier, either an internal cross-site user id
     *
     * @param cxenseId Cxense-specific user identifier
     */
    public void setCxenseId(String cxenseId) {
        Preconditions.checkStringForNullOrEmpty(cxenseId, "cxenseId");
        this.cxenseId = cxenseId;
    }
}
