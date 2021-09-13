package com.cxense.cxensesdk

import com.cxense.cxensesdk.model.WidgetItem
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

class WidgetItemAdapter {
    @FromJson
    fun fromJson(props: Map<String, Any>): WidgetItem =
        WidgetItem(
            props[TITLE]?.toString(),
            props[URL]?.toString(),
            props[CLICK_URL]?.toString(),
            props.filterKeys { it !in listOf(TITLE, URL, CLICK_URL) }
        )

    @Suppress("UNUSED_PARAMETER")
    @ToJson
    fun toJson(value: WidgetItem): Map<String, Any> {
        TODO("Not supported")
    }

    companion object {
        private const val TITLE = "title"
        private const val URL = "url"
        private const val CLICK_URL = "click_url"
    }
}
