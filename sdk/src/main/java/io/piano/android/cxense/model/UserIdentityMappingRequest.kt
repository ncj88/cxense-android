package io.piano.android.cxense.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Request object for getting/adding user identity mapping from server
 *
 */
@JsonClass(generateAdapter = true)
class UserIdentityMappingRequest(
    @Json(name = "cxid") val cxenseId: String,
    @Json(name = "type") val type: String,
    @Json(name = "id") val id: String? = null
)
