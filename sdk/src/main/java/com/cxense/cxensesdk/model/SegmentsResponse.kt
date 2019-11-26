package com.cxense.cxensesdk.model

import com.google.gson.annotations.SerializedName

class SegmentsResponse(
    @SerializedName("segments") val ids: List<String> = emptyList()
)
