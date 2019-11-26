package com.cxense.cxensesdk.model

import com.google.gson.annotations.SerializedName

class UserExternalDataRequest(
    @SerializedName("type") val type: String,
    @SerializedName("id") val id: String?,
    @SerializedName("filter") val filter: String?
)
