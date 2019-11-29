package com.cxense.cxensesdk.model

import com.google.gson.annotations.SerializedName

/**
 * Request object for getting/adding user identity mapping from server
 *
 */
class UserIdentityMappingRequest(
    @SerializedName("cxenseId") val cxenseId: String,
    @SerializedName("type") val type: String,
    @SerializedName("id") val id: String? = null
)
