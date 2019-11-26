package com.cxense.cxensesdk.model

class WidgetResponse(
    val items: List<WidgetItem> = emptyList(),
    val template: String? = null,
    val style: String? = null
)
