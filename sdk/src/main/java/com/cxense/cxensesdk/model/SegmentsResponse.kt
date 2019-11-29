package com.cxense.cxensesdk.model

import com.google.gson.annotations.SerializedName

/**
 * Response for segments from server.
 *
 */
class SegmentsResponse(
    @SerializedName("segments") val ids: List<String> = emptyList()
)
