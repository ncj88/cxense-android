package io.piano.android.cxense.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Request object for getting user segments from server.
 *
 */
@JsonClass(generateAdapter = true)
class UserSegmentRequest(
    @Json(name = "identities") val identities: List<UserIdentity>,
    @Json(name = "siteGroupIds") val siteGroups: List<String>,
)
