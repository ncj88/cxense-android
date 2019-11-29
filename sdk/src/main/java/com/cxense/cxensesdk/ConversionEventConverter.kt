package com.cxense.cxensesdk

import androidx.annotation.RestrictTo
import com.cxense.cxensesdk.db.EventRecord
import com.cxense.cxensesdk.model.ConversionEvent
import com.cxense.cxensesdk.model.Event
import com.google.gson.Gson

/**
 * Supports [ConversionEvent] to [EventRecord] converting
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
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
