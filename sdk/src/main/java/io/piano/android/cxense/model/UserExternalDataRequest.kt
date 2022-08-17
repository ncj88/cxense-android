package io.piano.android.cxense.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Request object for getting user external data from server
 *
 */
@JsonClass(generateAdapter = true)
class UserExternalDataRequest(
    @Json(name = "type") val type: String,
    @Json(name = "id") val id: String?,
    @Json(name = "filter") val filter: String?
)
