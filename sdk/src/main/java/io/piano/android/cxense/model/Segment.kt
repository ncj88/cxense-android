package io.piano.android.cxense.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Segment information
 */
@JsonClass(generateAdapter = true)
class Segment(
    @Json(name = "id") val id: String,
    @Json(name = "shortId") val shortId: String,
    @Json(name = "type") val type: SegmentType,
)
