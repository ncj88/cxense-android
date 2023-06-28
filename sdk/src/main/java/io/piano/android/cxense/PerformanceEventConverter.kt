package io.piano.android.cxense

import androidx.annotation.RestrictTo
import com.squareup.moshi.JsonAdapter
import io.piano.android.cxense.db.EventRecord
import io.piano.android.cxense.model.CustomParameter
import io.piano.android.cxense.model.Event
import io.piano.android.cxense.model.PerformanceEvent
import java.util.concurrent.TimeUnit

/**
 * Supports [PerformanceEvent] to [EventRecord] converting
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
class PerformanceEventConverter(
    private val jsonAdapter: JsonAdapter<PerformanceEvent>,
) : EventConverter() {
    override fun canConvert(event: Event): Boolean = event is PerformanceEvent

    internal fun extractQueryData(eventRecord: EventRecord): Pair<List<String>?, Map<String, String>>? =
        jsonAdapter.fromJson(eventRecord.data)?.run {
            val parameters = customParameters.asSequence().map {
                prepareKey(
                    PerformanceEvent.CUSTOM_PARAMETERS,
                    CustomParameter.GROUP,
                    CustomParameter.ITEM,
                    it.name
                ) to it.value
            }
            val ids = identities.asSequence().map {
                prepareKey(
                    PerformanceEvent.USER_IDS,
                    USER_TYPE,
                    USER_ID,
                    it.type
                ) to it.id
            }
            val pairs = sequenceOf(
                PerformanceEvent.TIME to time.toString(),
                PerformanceEvent.PRND to prnd.orEmpty(),
                PerformanceEvent.RND to rnd,
                PerformanceEvent.SITE_ID to siteId,
                PerformanceEvent.ORIGIN to origin,
                PerformanceEvent.TYPE to eventType,
                CONSENT to consentOptions.joinToString()
            ) + parameters + ids
            segments to pairs.toMap()
        }

    override fun toEventRecord(event: Event): EventRecord? =
        (event as? PerformanceEvent)?.run {
            EventRecord(
                eventType,
                eventId,
                jsonAdapter.toJson(this),
                prnd,
                rnd,
                TimeUnit.SECONDS.toMillis(time),
                mergeKey = mergeKey
            )
        }

    override fun update(oldRecord: EventRecord, event: Event): EventRecord =
        with(event as PerformanceEvent) {
            jsonAdapter.fromJson(oldRecord.data)?.let { old ->
                toEventRecord(
                    PerformanceEvent(
                        eventId ?: old.eventId,
                        identities.takeUnless { it.isEmpty() } ?: old.identities,
                        siteId,
                        origin,
                        eventType,
                        prnd ?: old.prnd,
                        old.time,
                        segments?.takeUnless { it.isEmpty() } ?: old.segments,
                        customParameters.takeUnless { it.isEmpty() } ?: old.customParameters,
                        consentOptions.takeUnless { it.isEmpty() } ?: old.consentOptions,
                        old.rnd
                    )
                )
            } ?: oldRecord
        }

    internal fun prepareKey(objectName: String, nameKey: String, valueKey: String, name: String): String =
        listOf(
            objectName,
            "$nameKey:$name",
            valueKey
        ).joinToString(separator = "/")

    companion object {
        private const val CONSENT = "con"
        private const val USER_ID = "id"
        private const val USER_TYPE = "type"
    }
}
