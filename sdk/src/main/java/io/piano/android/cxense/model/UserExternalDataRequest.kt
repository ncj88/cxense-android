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
    @Json(name = "filter") val filter: String?,
    @Json(name = "groups") val groups: List<String>?,
    @Json(name = "format") val format: ResponseFormat = ResponseFormat.LEGACY,
) {
    @JsonClass(generateAdapter = false)
    enum class ResponseFormat {
        @Deprecated("Will be replaced with [TYPED] in future")
        @Json(name = "legacy")
        LEGACY,

        @Json(name = "typed")
        TYPED,
    }
}
