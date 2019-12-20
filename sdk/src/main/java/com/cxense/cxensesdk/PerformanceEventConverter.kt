package com.cxense.cxensesdk

import androidx.annotation.RestrictTo
import com.cxense.cxensesdk.db.EventRecord
import com.cxense.cxensesdk.model.CustomParameter
import com.cxense.cxensesdk.model.Event
import com.cxense.cxensesdk.model.PerformanceEvent
import com.google.gson.Gson

/**
 * Supports [PerformanceEvent] to [EventRecord] converting
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
class PerformanceEventConverter(
    private val gson: Gson
) : EventConverter() {
    override fun canConvert(event: Event): Boolean = event is PerformanceEvent

    internal fun extractQueryData(eventRecord: EventRecord): Pair<List<String>?, Map<String, String>>? =
        gson.fromJson(eventRecord.data, PerformanceEvent::class.java)?.run {
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
                gson.toJson(this),
                prnd,
                rnd,
                time
            )
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
