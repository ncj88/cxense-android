package com.cxense.cxensesdk.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.List;

/**
 * User object.
 *
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-05).
 */
@SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
public final class User extends UserIdentity {
    @SerializedName("profile")
    List<UserProfile> profiles;
    @SerializedName("identities")
    List<UserIdentity> identities;

    protected User() {
    }

    public User(@NonNull String id, @NonNull String type) {
        super(id, type);
    }

    public User(@NonNull UserIdentity identity) {
        super(identity);
    }

    /**
     * Gets list of user generated content profiles.
     *
     * @return list of {@link UserProfile} objects
     */
    @NonNull
    public List<UserProfile> getProfiles() {
        return Collections.unmodifiableList(profiles);
    }

    /**
     * Gets list of user identities
     *
     * @return list of {@link UserIdentity} objects
     */
    @NonNull
    public List<UserIdentity> getIdentities() {
        return Collections.unmodifiableList(identities);
    }
}
