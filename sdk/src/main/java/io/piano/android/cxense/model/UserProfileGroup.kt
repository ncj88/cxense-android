package io.piano.android.cxense.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Group object for item in {@link UserProfile}
 *
 */
@JsonClass(generateAdapter = true)
class UserProfileGroup(
    @Json(name = "group") val group: String?,
    @Json(name = "count") val count: Int,
    @Json(name = "weight") val weight: Double
)
