package com.cxense.cxensesdk

import com.cxense.cxensesdk.db.EventRecord
import com.cxense.cxensesdk.model.Event

/**
 * Base class for all event converters.
 */
abstract class EventConverter {
    abstract fun canConvert(event: Event): Boolean

    abstract fun toEventRecord(event: Event): EventRecord?
}
