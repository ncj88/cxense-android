package io.piano.android.cxense.model

import com.squareup.moshi.JsonClass

/**
 * This class describes user for server.
 * @property ids Map from ID-types to IDs (String to String). ID-types are defined as a customer-prefix.
 * @property likes User's likes
 * @property dislikes User's dislikes
 */
@Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
@JsonClass(generateAdapter = true)
class ContentUser() {
    constructor(userId: String) : this() {
        ids[USI_ID] = userId
    }

    var ids: MutableMap<String, String> = mutableMapOf()
        internal set
    var likes: UserPreference? = null
    var dislikes: UserPreference? = null

    companion object {
        const val USI_ID = "usi"
    }
}
