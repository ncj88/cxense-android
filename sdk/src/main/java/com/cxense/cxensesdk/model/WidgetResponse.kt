package com.cxense.cxensesdk.model

import com.squareup.moshi.JsonClass

/**
 * Response for widget from server.
 */
@JsonClass(generateAdapter = true)
class WidgetResponse(
    val items: List<WidgetItem> = emptyList(),
    val template: String? = null,
    val style: String? = null
)
