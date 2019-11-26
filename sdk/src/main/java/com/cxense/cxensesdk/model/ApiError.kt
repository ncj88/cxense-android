package com.cxense.cxensesdk.model

import com.google.gson.annotations.SerializedName

class ApiError(
    @SerializedName("error") val error: String? = null
)
