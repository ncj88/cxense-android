package com.cxense.cxensesdk.model

import com.google.gson.annotations.SerializedName

/**
 * Response for data associated with the user(s) from server.
 *
 */
class UserExternalDataResponse(
    @SerializedName("data") val items: List<UserExternalData> = emptyList()
)
