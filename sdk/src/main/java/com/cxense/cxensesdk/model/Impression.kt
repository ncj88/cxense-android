package com.cxense.cxensesdk.model

import com.google.gson.annotations.SerializedName

/**
 * Describes impression data
 *
 */
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
