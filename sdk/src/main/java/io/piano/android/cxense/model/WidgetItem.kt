package io.piano.android.cxense.model

/**
 * Widget item
 * @property title Item title
 * @property url Item url
 * @property clickUrl Click url for item
 * @property properties Item custom properties
 */
@Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
class WidgetItem(
    val title: String?,
    val url: String?,
    val clickUrl: String?,
    val properties: Map<String, Any>,
)
