package io.piano.android.cxense.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Request object for reporting widgets visibilities to server.
 *
 */
@JsonClass(generateAdapter = true)
class WidgetVisibilityReport(
    @Json(name = "impressions") val impressions: List<Impression>,
)
