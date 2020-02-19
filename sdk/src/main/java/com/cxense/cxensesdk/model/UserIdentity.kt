package com.cxense.cxensesdk.model

import com.google.gson.annotations.SerializedName

/**
 * User identity object.
 * @param type user identifier type
 * @property type user identifier type
 * @property id user identifier
 */
open class UserIdentity constructor(
    type: String,
    @SerializedName("id") val id: String
) {
    @SerializedName("type")
    val type: String = type.also {
        require(it == CX_USER_TYPE || it.matches(USER_TYPE_REGEX.toRegex())) {
            """Type should be "cx" or a three character lower-case alpha-numeric string"""
        }
    }

    companion object {
        private const val CX_USER_TYPE = "cx"
        private const val USER_TYPE_REGEX = "\\w{3}"
    }
}
