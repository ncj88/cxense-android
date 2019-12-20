package com.cxense.cxensesdk.model

/**
 * Request widget data object for server
 */
class WidgetRequest(
    val widgetId: String,
    val consent: List<String>,
    val context: WidgetContext? = null,
    val user: ContentUser? = null,
    val tag: String? = null,
    val prnd: String? = null
)
