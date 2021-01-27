package com.cxense.cxensesdk.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Stored key-values for the user.
 * @param group group name
 * @property group group name
 *
 * @param item the item which is to be associated with the group name.
 * @property item the item which is to be associated with the group name.
 *
 * @throws IllegalArgumentException if [group] or [item] doesn't meet a criteria.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
@JsonClass(generateAdapter = true)
class ExternalItem(
    group: String,
    item: String
) {
    @Json(name = "group")
    val group: String = group.also {
        require(it.matches(GROUP_REGEXP.toRegex())) {
            "Group should not be empty and can contains only letters, digits or dash," +
                " max length is $GROUP_NAME_MAX_LENGTH"
        }
    }

    @Json(name = "item")
    val item: String = item.also {
        require(it.length <= ITEM_NAME_MAX_LENGTH) {
            "Item can't be longer than $ITEM_NAME_MAX_LENGTH symbols"
        }
    }

    companion object {
        private const val GROUP_NAME_MAX_LENGTH = 26
        private const val GROUP_REGEXP = "^[a-zA-Z\\d-]{1,$GROUP_NAME_MAX_LENGTH}$"
        private const val ITEM_NAME_MAX_LENGTH = 100
    }
}
