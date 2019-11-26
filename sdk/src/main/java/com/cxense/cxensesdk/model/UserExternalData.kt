package com.cxense.cxensesdk.model

import com.google.gson.annotations.SerializedName

class UserExternalData private constructor(
    type: String,
    id: String,
    externalItems: List<ExternalItem>
) : UserIdentity(type, id) {

    @SerializedName("profile")
    val items: List<ExternalItem> = externalItems.map {
        ExternalItem("$type-${it.group}", it.item)
    }

    data class Builder(
        var identity: UserIdentity,
        var externalItems: MutableList<ExternalItem> = mutableListOf()
    ) {
        fun addExternalItems(vararg externalItems: ExternalItem) = apply { this.externalItems.addAll(externalItems) }
        fun addExternalItems(externalItems: Iterable<ExternalItem>) = apply { this.externalItems.addAll(externalItems) }
        fun identity(identity: UserIdentity) = apply { this.identity = identity }

        fun build(): UserExternalData {
            check(externalItems.size <= MAX_PROFILE_ITEMS) {
                "Too many profile items. Current size: ${externalItems.size}, allowed max size: $MAX_PROFILE_ITEMS"
            }
            return UserExternalData(identity.type, identity.id, externalItems)
        }
    }

    companion object {
        const val MAX_PROFILE_ITEMS = 40
    }
}
