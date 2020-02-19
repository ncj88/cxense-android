package com.cxense.cxensesdk.model

import com.google.gson.annotations.SerializedName

/**
 * Request object for getting user from server
 *
 */
class UserDataRequest(
    userIdentity: UserIdentity,
    @SerializedName("groups") val groups: List<String>? = null,
    @SerializedName("recent") val recent: Boolean? = null,
    @SerializedName("identityTypes") val identityTypes: List<String>? = null
) : UserIdentity(userIdentity.type, userIdentity.id)
