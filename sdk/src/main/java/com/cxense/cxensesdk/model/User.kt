package com.cxense.cxensesdk.model

import com.google.gson.annotations.SerializedName

/**
 * User object.
 *
 */
class User(
    type: String,
    id: String,
    @SerializedName("identities") val identities: List<UserIdentity>
) : UserIdentity(type, id)
