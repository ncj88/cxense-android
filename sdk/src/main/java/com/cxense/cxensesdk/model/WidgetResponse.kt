package com.cxense.cxensesdk.model

/**
 * Response for widget from server.
 */
class WidgetResponse(
    val items: List<WidgetItem> = emptyList(),
    val template: String? = null,
    val style: String? = null
)
