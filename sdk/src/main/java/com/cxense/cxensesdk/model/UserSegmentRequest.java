package com.cxense.cxensesdk.model;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Request object for getting user segments from server.
 *
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-05).
 */
@SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
public final class UserSegmentRequest {
    @JsonProperty("siteGroupIds")
    List<String> siteGroups;
    @JsonProperty("identities")
    List<UserIdentity> identities;

    public UserSegmentRequest(@Nullable List<UserIdentity> identities, @Nullable List<String> siteGroups) {
        if (siteGroups != null)
            this.siteGroups = new ArrayList<>(siteGroups);
        if (identities != null)
            this.identities = new ArrayList<>(identities);
    }
}
