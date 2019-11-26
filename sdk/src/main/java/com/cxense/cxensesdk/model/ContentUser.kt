package com.cxense.cxensesdk.model

/**
 * This class describes user for server.
 *
 */
@Suppress("unused") // Public API.
class ContentUser() {
    constructor(userId: String) : this() {
        ids[USI_ID] = userId
    }

    /**
     * Map from ID-types to IDs (String to String). ID-types are defined as a customer-prefix.
     */
    val ids: MutableMap<String, String> = mutableMapOf()
    /**
     * User's likes
     */
    var likes: UserPreference? = null
    /**
     * User's dislikes
     */
    var dislikes: UserPreference? = null

    companion object {
        const val USI_ID = "usi"
    }
}
