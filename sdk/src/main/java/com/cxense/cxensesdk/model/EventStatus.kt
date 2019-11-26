package com.cxense.cxensesdk.model

class EventStatus(
    val eventId: String?,
    val isSent: Boolean,
    val exception: Exception? = null
)
