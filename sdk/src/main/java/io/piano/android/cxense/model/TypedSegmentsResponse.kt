package io.piano.android.cxense.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Response for segments from server.
 *
 */
@JsonClass(generateAdapter = true)
class TypedSegmentsResponse(
    @Json(name = "segments") val segments: List<Segment> = emptyList(),
)
