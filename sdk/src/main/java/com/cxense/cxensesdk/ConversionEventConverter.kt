package com.cxense.cxensesdk

import com.cxense.cxensesdk.db.EventRecord
import com.cxense.cxensesdk.model.ConversionEvent
import com.cxense.cxensesdk.model.Event
import com.google.gson.Gson

class ConversionEventConverter(
    private val gson: Gson
) : EventConverter() {
    override fun canConvert(event: Event): Boolean = event is ConversionEvent

    override fun toEventRecord(event: Event): EventRecord? =
        (event as? ConversionEvent)?.run {
            EventRecord(
                ConversionEvent.EVENT_TYPE,
                eventId,
                gson.toJson(this)
            )
        }
}