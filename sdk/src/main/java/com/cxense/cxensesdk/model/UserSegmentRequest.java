package com.cxense.cxensesdk.model;

import android.support.annotation.NonNull;

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

    public UserSegmentRequest(@NonNull List<UserIdentity> identities, @NonNull List<String> siteGroups) {
        this.siteGroups = new ArrayList<>(siteGroups);
        this.identities = new ArrayList<>(identities);
    }
}
