package com.cxense.cxensesdk.model

import com.google.gson.annotations.SerializedName

/**
 * User object.
 *
 */
class User(
    type: String,
    id: String,
    @SerializedName("profile") val profiles: List<UserProfile>,
    @SerializedName("identities") val identities: List<UserIdentity>
) : UserIdentity(type, id)
