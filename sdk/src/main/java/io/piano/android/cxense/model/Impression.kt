package io.piano.android.cxense.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Describes impression data
 * @param clickUrl click URL
 * @property clickUrl click URL
 *
 * @param seconds visibility seconds
 * @property seconds visibility seconds
 */
@Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
@JsonClass(generateAdapter = true)
class Impression(
    clickUrl: String,
    seconds: Int
) {
    @Json(name = "clickUrl")
    val clickUrl: String = clickUrl.also {
        require(it.isNotEmpty()) {
            "clickUrl should be filled"
        }
    }

    @Json(name = "visibilitySeconds")
    val seconds: Int = seconds.also {
        require(it > 0) {
            "Seconds value should be more than 0"
        }
    }
}
