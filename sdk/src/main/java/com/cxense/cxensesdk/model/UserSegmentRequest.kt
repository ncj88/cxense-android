package com.cxense.cxensesdk.model

import com.google.gson.annotations.SerializedName

/**
 * Request object for getting user segments from server.
 *
 */
class UserSegmentRequest(
    identities: List<UserIdentity>,
    siteGroups: List<String>
) {
    @SerializedName("identities")
    val identities: List<UserIdentity> = identities.also {
        require(it.isNotEmpty()) {
            "You should provide at least one user identity"
        }
    }
    @SerializedName("siteGroupIds")
    val siteGroups: List<String> = siteGroups
        .filterNot { it.isEmpty() }
        .also {
            require(it.isNotEmpty()) {
                "You should provide at least one not empty site group id"
            }
        }
}
