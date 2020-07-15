package com.cxense.cxensesdk.model

import com.google.gson.annotations.SerializedName

/**
 * Request object for getting user segments from server.
 *
 */
class UserSegmentRequest(
    @SerializedName("identities") private val identities: List<UserIdentity>,
    @SerializedName("siteGroupIds") private val siteGroups: List<String>
)
