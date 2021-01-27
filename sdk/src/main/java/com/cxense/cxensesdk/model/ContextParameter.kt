package com.cxense.cxensesdk.model

import com.squareup.moshi.JsonClass

/**
 * Context parameter that replace the placeholders are passed from the widget data request.
 * @param key Defines the key we use for the replacement
 * @param value Defines the value associated with key or the default value
 */
@Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
@JsonClass(generateAdapter = true)
class ContextParameter(
    val key: String,
    val value: String
)
