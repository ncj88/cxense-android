package com.cxense.cxensesdk.model

import com.cxense.cxensesdk.RawJsonAdapter
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName

class EventDataRequest(
    @SerializedName("events") @JsonAdapter(RawJsonAdapter::class) val events: List<String> = emptyList()
)