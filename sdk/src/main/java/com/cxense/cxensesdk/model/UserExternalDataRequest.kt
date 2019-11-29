package com.cxense.cxensesdk.model

import com.google.gson.annotations.SerializedName

/**
 * Request object for getting user external data from server
 *
 */
class UserExternalDataRequest(
    @SerializedName("type") val type: String,
    @SerializedName("id") val id: String?,
    @SerializedName("filter") val filter: String?
)
