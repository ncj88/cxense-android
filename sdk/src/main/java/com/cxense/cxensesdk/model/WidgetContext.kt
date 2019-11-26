package com.cxense.cxensesdk.model

import java.util.Collections

class WidgetContext(
    val url: String,
    val referrer: String?,
    val pageclass: String?,
    val sentiment: String?,
    val recommending: Boolean?,
    val categories: MutableMap<String, String>,
    val keywords: MutableList<String>,
    val neighbors: MutableList<String>,
    val parameters: MutableList<ContextParameter>
) {
    data class Builder(
        var url: String,
        var referrer: String? = null,
        var pageclass: String? = null,
        var sentiment: String? = null,
        var recommending: Boolean? = null,
        var categories: MutableMap<String, String> = mutableMapOf(),
        var keywords: MutableList<String> = mutableListOf(),
        var neighbors: MutableList<String> = mutableListOf(),
        var parameters: MutableList<ContextParameter> = mutableListOf()
    ) {
        fun url(url: String) = apply { this.url = url }
        fun referrer(referrer: String?) = apply { this.referrer = referrer }
        fun pageclass(pageclass: String?) = apply { this.pageclass = pageclass }
        fun sentiment(sentiment: String?) = apply { this.sentiment = sentiment }
        fun recommending(recommending: Boolean?) = apply { this.recommending = recommending }
        fun categories(categories: MutableMap<String, String>) = apply { this.categories = categories }
        fun keywords(keywords: MutableList<String>) = apply { this.keywords = keywords }
        fun neighbors(neighbors: MutableList<String>) = apply { this.neighbors = neighbors }
        fun parameters(parameters: MutableList<ContextParameter>) = apply { this.parameters = parameters }

        fun build(): WidgetContext = WidgetContext(
            url,
            referrer,
            pageclass,
            sentiment,
            recommending,
            Collections.unmodifiableMap(categories),
            Collections.unmodifiableList(keywords),
            Collections.unmodifiableList(neighbors),
            Collections.unmodifiableList(parameters)
        )
    }
}
