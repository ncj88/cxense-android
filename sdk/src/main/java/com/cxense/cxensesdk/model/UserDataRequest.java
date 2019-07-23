package com.cxense.cxensesdk.model;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Request object for getting user from server
 *
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-05).
 */
@SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
public final class UserDataRequest extends UserIdentity {
    @SerializedName("groups")
    List<String> groups;
    @SerializedName("recent")
    Boolean isRecent;
    @SerializedName("identityTypes")
    List<String> identityTypes;

    public UserDataRequest(@NonNull UserIdentity identity, @Nullable Collection<String> groups, @Nullable Boolean recent,
                           @Nullable Collection<String> identityTypes) {
        super(identity);
        isRecent = recent;
        if (groups != null)
            this.groups = new ArrayList<>(groups);
        if (identityTypes != null)
            this.identityTypes = new ArrayList<>(identityTypes);
    }
}
