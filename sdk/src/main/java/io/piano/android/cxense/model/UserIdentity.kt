package io.piano.android.cxense.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * User identity object.
 * @param type user identifier type
 * @property type user identifier type
 * @property id user identifier
 */
@JsonClass(generateAdapter = true)
open class UserIdentity constructor(
    type: String,
    @Json(name = "id") val id: String,
) {
    @Json(name = "type")
    val type: String = type.also {
        require(it == CX_USER_TYPE || it.matches(USER_TYPE_REGEX.toRegex())) {
            """Type should be "cx" or a three character lower-case alpha-numeric string"""
        }
    }

    companion object {
        internal const val CX_USER_TYPE = "cx"
        private const val USER_TYPE_REGEX = "\\w{3}"
    }
}
