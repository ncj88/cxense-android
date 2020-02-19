package com.cxense.cxensesdk.model

import com.google.gson.annotations.SerializedName

/**
 * User generated content profile
 *
 */
class UserProfile(
    @SerializedName("item") val item: String?,
    @SerializedName("groups") val groups: List<UserProfileGroup>?
)
