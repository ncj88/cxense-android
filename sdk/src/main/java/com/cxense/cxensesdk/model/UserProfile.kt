package com.cxense.cxensesdk.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * User generated content profile
 *
 */
@JsonClass(generateAdapter = true)
class UserProfile(
    @Json(name = "item") val item: String?,
    @Json(name = "groups") val groups: List<UserProfileGroup>?
)
