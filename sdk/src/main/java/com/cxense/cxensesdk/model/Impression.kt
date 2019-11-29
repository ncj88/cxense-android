package com.cxense.cxensesdk.model

import com.google.gson.annotations.SerializedName

/**
 * Describes impression data
 * @param clickUrl click URL
 * @property clickUrl click URL
 *
 * @param seconds visibility seconds
 * @property seconds visibility seconds
 */
@Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
class Impression(
    clickUrl: String,
    seconds: Int
) {
    @SerializedName("clickUrl")
    val clickUrl: String = clickUrl.also {
        require(it.isNotEmpty()) {
            "clickUrl should be filled"
        }
    }

    @SerializedName("visibilitySeconds")
    val seconds: Int = seconds.also {
        require(it > 0) {
            "Seconds value should be more than 0"
        }
    }
}
