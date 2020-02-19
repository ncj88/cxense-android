package com.cxense.cxensesdk.model

import com.google.gson.annotations.SerializedName

/**
 * Group object for item in {@link UserProfile}
 *
 */
class UserProfileGroup(
    @SerializedName("group") val group: String?,
    @SerializedName("count") val count: Int,
    @SerializedName("weight") val weight: Double
)
