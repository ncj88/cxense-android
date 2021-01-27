package com.cxense.cxensesdk.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Api Error answer
 *
 */
@JsonClass(generateAdapter = true)
class ApiError(
    @Json(name = "error") val error: String? = null
)
