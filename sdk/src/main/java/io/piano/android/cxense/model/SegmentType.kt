package io.piano.android.cxense.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = false)
enum class SegmentType {
    @Json(name = "traffic")
    TRAFFIC,

    @Json(name = "external")
    EXTERNAL,

    @Json(name = "lookalike")
    LOOKALIKE,
}
