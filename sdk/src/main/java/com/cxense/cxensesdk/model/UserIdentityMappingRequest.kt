package com.cxense.cxensesdk.model

import com.google.gson.annotations.SerializedName

class UserIdentityMappingRequest(
    @SerializedName("cxenseId") val cxenseId: String,
    @SerializedName("type") val type: String,
    @SerializedName("id") val id: String? = null
)