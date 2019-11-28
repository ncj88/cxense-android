package com.cxense.cxensesdk.model

/**
 * User preferences. Used for likes/dislikes.
 */
@Suppress("unused") // Public API.
class UserPreference(
    /**
     * List of categories.
     */
    val categories: List<String>,
    /**
     * A boost value between 0.0 and 100.0 indicating the level of boost to give for the list of categories.
     * Note that 0.0 (the default) has the special handling of being a filter instead of a boost.
     */
    val boost: Double
)
