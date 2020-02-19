package com.cxense.cxensesdk.model

/**
 * Context parameter that replace the placeholders are passed from the widget data request.
 * @param key Defines the key we use for the replacement
 * @param value Defines the value associated with key or the default value
 */
@Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
class ContextParameter(
    val key: String,
    val value: String
)
