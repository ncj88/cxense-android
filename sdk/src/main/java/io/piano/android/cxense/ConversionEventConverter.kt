package io.piano.android.cxense

import androidx.annotation.RestrictTo
import com.squareup.moshi.JsonAdapter
import io.piano.android.cxense.db.EventRecord
import io.piano.android.cxense.model.ConversionEvent
import io.piano.android.cxense.model.Event

/**
 * Supports [ConversionEvent] to [EventRecord] converting
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
class ConversionEventConverter(
    private val jsonAdapter: JsonAdapter<ConversionEvent>
) : EventConverter() {
    override fun canConvert(event: Event): Boolean = event is ConversionEvent

    override fun toEventRecord(event: Event): EventRecord? =
        (event as? ConversionEvent)?.run {
            EventRecord(
                ConversionEvent.EVENT_TYPE,
                eventId,
                jsonAdapter.toJson(this),
                mergeKey = mergeKey
            )
        }

    override fun update(oldRecord: EventRecord, event: Event): EventRecord =
        with(event as ConversionEvent) {
            jsonAdapter.fromJson(oldRecord.data)?.let { old ->
                toEventRecord(
                    ConversionEvent(
                        identities.takeUnless { it.isEmpty() } ?: old.identities,
                        siteId,
                        consentOptions.takeUnless { it.isEmpty() } ?: old.consentOptions,
                        productId,
                        funnelStep,
                        price ?: old.price,
                        renewalFrequency ?: old.renewalFrequency,
                        eventType
                    )
                )
            } ?: oldRecord
        }
}
