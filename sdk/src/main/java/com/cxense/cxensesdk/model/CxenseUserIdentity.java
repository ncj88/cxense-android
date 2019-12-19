package com.cxense.cxensesdk.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cxense.cxensesdk.Preconditions;
import com.google.gson.annotations.SerializedName;

/**
 * Cxense user identity object.
 *
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-05).
 */
@SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
public final class CxenseUserIdentity extends UserIdentity {
    @SerializedName("cxid")
    private String cxenseId;

    /**
     * @param identity User identity object
     * @param cxenseId The Cxense-specific user identifier, either an internal cross-site user id
     */
    public CxenseUserIdentity(@NonNull UserIdentity identity, @NonNull String cxenseId) {
        super(identity);
        setCxenseId(cxenseId);
    }

    /**
     * @param type     The customer-specific identifier type as registered with Cxense
     * @param cxenseId The Cxense-specific user identifier, either an internal cross-site user id
     */
    public CxenseUserIdentity(@Nullable String type, @NonNull String cxenseId) {
        super(type);
        setCxenseId(cxenseId);
    }

    /**
     * Gets the Cxense-specific user identifier, either an internal cross-site user id
     *
     * @return Cxense-specific user identifier
     */
    @NonNull
    public String getCxenseId() {
        return cxenseId;
    }

    /**
     * Sets the Cxense-specific user identifier, either an internal cross-site user id
     *
     * @param cxenseId Cxense-specific user identifier
     */
    public void setCxenseId(@NonNull String cxenseId) {
        Preconditions.checkStringForNullOrEmpty(cxenseId, "cxenseId");
        this.cxenseId = cxenseId;
    }
}
