package io.piano.android.cxense.model

import com.squareup.moshi.JsonClass
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import java.util.Collections

/**
 * Provides information about the current context / page for Widget
 * @property url url for widget, required.
 * @property referrer referrer url.
 * @property pageclass the pageclass of the current page.
 * @property sentiment the sentiment of the current page.
 * @property recommending recs-recommending setting of the current page.
 * @property categories map for categories of the current page.
 * @property keywords list of keywords describing the context.
 * @property neighbors list of linked articles IDs.
 * @property parameters list of [ContextParameter] objects.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
@JsonClass(generateAdapter = true)
class WidgetContext internal constructor(
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
    /**
     * @constructor Initialize builder for [WidgetContext]
     * @property url defines url for widget, required.
     * @property referrer defines referrer url.
     * @property pageclass the pageclass of the current page.
     * @property sentiment the sentiment of the current page.
     * @property recommending recs-recommending setting of the current page.
     * @property categories map for categories of the current page.
     * @property keywords list of keywords describing the context.
     * @property neighbors list of linked articles IDs.
     * @property parameters list of [ContextParameter] objects.
     */
    data class Builder @JvmOverloads constructor(
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
        /**
         * Sets url
         * @param url url for widget.
         */
        fun url(url: String) = apply { this.url = url }

        /**
         * Sets referrer
         * @param referrer referrer url.
         */
        fun referrer(referrer: String?) = apply { this.referrer = referrer }

        /**
         * Sets pageclass
         * @param pageclass the pageclass of the current page.
         */
        fun pageclass(pageclass: String?) = apply { this.pageclass = pageclass }

        /**
         * Sets sentiment
         * @param sentiment the sentiment of the current page.
         */
        fun sentiment(sentiment: String?) = apply { this.sentiment = sentiment }

        /**
         * Sets recommending flag
         * @param recommending recs-recommending setting of the current page.
         */
        fun recommending(recommending: Boolean?) = apply { this.recommending = recommending }

        /**
         * Sets categories
         * @param categories map for categories of the current page.
         */
        fun categories(categories: MutableMap<String, String>) = apply { this.categories = categories }

        /**
         * Sets keywords
         * @param keywords list of keywords describing the context.
         */
        fun keywords(keywords: MutableList<String>) = apply { this.keywords = keywords }

        /**
         * Sets neighbors
         * @param neighbors list of linked articles IDs.
         */
        fun neighbors(neighbors: MutableList<String>) = apply { this.neighbors = neighbors }

        /**
         * Sets parameters
         * @param parameters list of [ContextParameter] objects.
         */
        fun parameters(parameters: MutableList<ContextParameter>) = apply { this.parameters = parameters }

        /**
         * Builds widget context
         * @throws [IllegalArgumentException] if constraints failed
         */
        fun build(): WidgetContext {
            check(url.toHttpUrlOrNull() != null) {
                "You should provide valid url as source"
            }
            referrer?.let {
                check(it.toHttpUrlOrNull() != null) {
                    "You should provide valid url as referrer"
                }
            }
            return WidgetContext(
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
}
