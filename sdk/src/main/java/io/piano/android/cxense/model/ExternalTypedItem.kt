package io.piano.android.cxense.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Stored key-values for the user.
 * @property group group name
 * @property item the item which is to be associated with the group name.
 *
 * @throws IllegalArgumentException if [group] doesn't meet a criteria.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
@JsonClass(generateAdapter = true)
class ExternalTypedItem internal constructor(
    @Json(name = "group") val group: String,
    @Json(name = "item") val item: TypedItem,
) {
    init {
        require(group.matches(GROUP_REGEXP)) {
            "Group should not be empty and can contains only letters, digits or dash," +
                " max length is ${GROUP_NAME_MAX_LENGTH}"
        }
    }
    companion object {
        private const val GROUP_NAME_MAX_LENGTH = 26
        private val GROUP_REGEXP = "^[a-zA-Z\\d-]{1,$GROUP_NAME_MAX_LENGTH}$".toRegex()

        @JvmName("create")
        @JvmStatic
        operator fun invoke(group: String, item: TypedItem): ExternalTypedItem {
            require(item != TypedItem.Unknown)
            return ExternalTypedItem(group, item)
        }
    }
}
