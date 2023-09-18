package io.piano.android.cxense.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import io.piano.android.cxense.model.CustomParameter.Companion.MAX_CUSTOM_PARAMETER_KEY_LENGTH
import io.piano.android.cxense.model.CustomParameter.Companion.MAX_CUSTOM_PARAMETER_VALUE_LENGTH

/**
 * Customer-defined parameter object
 * @param name Parameter name, e.g., "campaign", "adspace" or "creative".
 * @property name Parameter name, e.g., "campaign", "adspace" or "creative".
 *
 * @param value Parameter value, e.g. "sale", "42".
 * @property value Parameter value, e.g. "sale", "42".
 *
 * @throws IllegalArgumentException if [name] longer than [MAX_CUSTOM_PARAMETER_KEY_LENGTH] or [value] longer than [MAX_CUSTOM_PARAMETER_VALUE_LENGTH]
 */
@Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
@JsonClass(generateAdapter = true)
class CustomParameter(
    name: String,
    value: String,
) {
    @Json(name = GROUP)
    val name: String = name.also {
        require(it.length <= MAX_CUSTOM_PARAMETER_KEY_LENGTH) {
            "Name can't be longer than $MAX_CUSTOM_PARAMETER_KEY_LENGTH symbols"
        }
    }

    @Json(name = ITEM)
    val value: String = value.also {
        require(it.length <= MAX_CUSTOM_PARAMETER_VALUE_LENGTH) {
            "Value can't be longer than $MAX_CUSTOM_PARAMETER_VALUE_LENGTH symbols"
        }
    }

    companion object {
        const val GROUP = "group"
        const val ITEM = "item"
        const val MAX_CUSTOM_PARAMETER_KEY_LENGTH = 20
        const val MAX_CUSTOM_PARAMETER_VALUE_LENGTH = 256
    }
}
