package io.piano.android.cxense.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Response for segments from server.
 *
 */
@JsonClass(generateAdapter = true)
class SegmentsResponse(
    @Json(name = "segments") val ids: List<String> = emptyList(),
)
