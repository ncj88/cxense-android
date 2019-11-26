package com.cxense.cxensesdk.model

import com.google.gson.annotations.SerializedName

open class UserIdentity constructor(
    type: String,
    @SerializedName(ID) val id: String
) {
    @SerializedName(TYPE)
    val type: String = type.also {
        require(it == CX_USER_TYPE || it.matches(USER_TYPE_REGEX.toRegex())) {
            """Type should be "cx" or a three character lower-case alpha-numeric string"""
        }
    }

    companion object {
        const val ID = "id"
        const val TYPE = "type"
        private const val CX_USER_TYPE = "cx"
        private const val USER_TYPE_REGEX = "\\w{3}"
    }
}