package com.cxense.cxensesdk.model

import com.google.gson.annotations.SerializedName

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
    val siteGroups: List<String> = siteGroups.also {
        require(it.isNotEmpty()) {
            "You should provide at least one site group"
        }
    }
}
