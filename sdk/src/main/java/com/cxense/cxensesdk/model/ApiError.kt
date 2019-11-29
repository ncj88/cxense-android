package com.cxense.cxensesdk.model

import com.google.gson.annotations.SerializedName

/**
 * Api Error answer
 *
 */
class ApiError(
    @SerializedName("error") val error: String? = null
)
