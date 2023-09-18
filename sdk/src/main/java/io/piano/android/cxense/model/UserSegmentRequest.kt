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
    @Json(name = "siteGroupIds") val siteGroups: List<String>?,
    @Json(name = "candidateSegments") val candidateSegments: List<CandidateSegment>? = null,
    @Json(name = "format") val format: ResponseFormat = ResponseFormat.CX,
    @Json(name = "segmentFormat") val segmentFormat: SegmentFormat = SegmentFormat.STANDARD,
) {
    @JsonClass(generateAdapter = false)
    enum class ResponseFormat {
        @Deprecated("Will be replaced with [CX_TYPED] in future")
        @Json(name = "cx")
        CX,

        @Json(name = "cx_typed")
        CX_TYPED,
    }

    @JsonClass(generateAdapter = false)
    enum class SegmentFormat {
        @Json(name = "standard")
        STANDARD,

        @Json(name = "short_ids")
        SHORT_IDS,
    }
}
