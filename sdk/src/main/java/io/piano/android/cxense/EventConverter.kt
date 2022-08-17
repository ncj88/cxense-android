package io.piano.android.cxense

import io.piano.android.cxense.db.EventRecord
import io.piano.android.cxense.model.Event

/**
 * Base class for all event converters.
 */
abstract class EventConverter {
    abstract fun canConvert(event: Event): Boolean

    abstract fun toEventRecord(event: Event): EventRecord?

    abstract fun update(oldRecord: EventRecord, event: Event): EventRecord
}
