package io.piano.android.cxense.model

import com.squareup.moshi.JsonClass

/**
 * Request widget data object for server
 */
@JsonClass(generateAdapter = true)
class WidgetRequest(
    val widgetId: String,
    val consent: List<String>,
    val context: WidgetContext? = null,
    val user: ContentUser? = null,
    val tag: String? = null,
    val prnd: String? = null,
    val experienceId: String? = null
)
