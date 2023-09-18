package io.piano.android.cxense.model

import com.squareup.moshi.JsonClass

/**
 * User preferences. Used for likes/dislikes.
 * @property categories List of categories.
 * @property boost A boost value between 0.0 and 100.0 indicating the level of boost to give for the list of categories.
 * Note that 0.0 (the default) has the special handling of being a filter instead of a boost.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
@JsonClass(generateAdapter = true)
class UserPreference(
    val categories: List<String>,
    val boost: Double,
)
