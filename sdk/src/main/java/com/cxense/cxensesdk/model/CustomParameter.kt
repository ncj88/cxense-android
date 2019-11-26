package com.cxense.cxensesdk.model

import com.google.gson.annotations.SerializedName

/**
 * Customer-defined parameter object
 *
 */
class CustomParameter(
    name: String,
    value: String
) {
    @SerializedName(GROUP)
    val name: String = name.also {
        require(it.length <= MAX_CUSTOM_PARAMETER_KEY_LENGTH) {
            "Name can't be longer than $MAX_CUSTOM_PARAMETER_KEY_LENGTH symbols"
        }
    }
    @SerializedName(ITEM)
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
