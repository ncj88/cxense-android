package com.cxense.cxensesdk.model

class WidgetRequest(
    val widgetId: String,
    val consent: List<String>,
    val context: WidgetContext? = null,
    val user: ContentUser? = null,
    val tag: String? = null,
    val prnd: String? = null
)
