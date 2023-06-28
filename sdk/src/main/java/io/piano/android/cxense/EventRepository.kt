package io.piano.android.cxense

import android.content.ContentValues
import android.provider.BaseColumns
import io.piano.android.cxense.db.DatabaseHelper
import io.piano.android.cxense.db.EventRecord
import io.piano.android.cxense.model.ConversionEvent
import io.piano.android.cxense.model.Event
import io.piano.android.cxense.model.EventStatus
import io.piano.android.cxense.model.PageViewEvent
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * Repository for saving/getting events via local database.
 */
class EventRepository(
    private val configuration: CxenseConfiguration,
    private val databaseHelper: DatabaseHelper,
    private val eventConverters: List<EventConverter>,
) {
    fun putEventsInDatabase(events: Array<out Event>) {
        events.forEach { e ->
            try {
                eventConverters.firstOrNull {
                    it.canConvert(e)
                }?.buildEventRecord(e)?.let {
                    putEventRecordInDatabase(it)
                }
            } catch (ex: Exception) {
                Timber.e(ex, "Error at pushing event")
            }
        }
    }

    fun putEventRecordInDatabase(eventRecord: EventRecord): Long = databaseHelper.save(eventRecord)

    fun deleteOutdatedEvents(outdatePeriod: Long): Int =
        databaseHelper.delete(
            "${EventRecord.TIME} < ?",
            (System.currentTimeMillis() - outdatePeriod).toString()
        )

    fun getNotSubmittedPvEvents() = getEvents(
        "$NOT_SENT_FILTER AND ${EventRecord.TYPE} = ?",
        arrayOf(PageViewEvent.EVENT_TYPE)
    )

    fun getNotSubmittedDmpEvents() = getEvents(
        "$NOT_SENT_FILTER AND ${EventRecord.TYPE} <> ? AND ${EventRecord.TYPE} <> ?",
        arrayOf(PageViewEvent.EVENT_TYPE, ConversionEvent.EVENT_TYPE)
    )

    fun getNotSubmittedConversionEvents() = getEvents(
        "$NOT_SENT_FILTER AND ${EventRecord.TYPE} = ?",
        arrayOf(ConversionEvent.EVENT_TYPE)
    )

    internal fun getEvents(
        selection: String?,
        selectionArgs: Array<out String?>?,
        limit: String? = null,
    ): List<EventRecord> =
        databaseHelper.query(
            selection = selection,
            selectionArgs = selectionArgs,
            orderBy = "${EventRecord.TIME} ASC",
            limit = limit
        ).map { it.toEventRecord() }

    fun getPvEventFromDatabase(eventId: String): EventRecord? =
        databaseHelper.query(
            selection = "${EventRecord.CUSTOM_ID} = ? AND ${EventRecord.TYPE} = ?",
            selectionArgs = arrayOf(eventId, PageViewEvent.EVENT_TYPE),
            orderBy = "${EventRecord.TIME} DESC",
            limit = "1"
        ).firstOrNull()?.toEventRecord()

    fun getEventStatuses(): List<EventStatus> =
        databaseHelper.query(
            columns = arrayOf(EventRecord.CUSTOM_ID, EventRecord.IS_SENT),
            orderBy = "${EventRecord.TIME} ASC"
        ).map {
            EventStatus(
                it.getAsString(EventRecord.CUSTOM_ID),
                it.getAsBoolean(EventRecord.IS_SENT)
            )
        }

    fun putEventTime(eventId: String, activeTime: Long) {
        try {
            val record = getPvEventFromDatabase(eventId) ?: return
            val time = activeTime.takeIf { it > 0 }
                ?: TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - record.timestamp)
            eventConverters.mapNotNull { it as? PageViewEventConverter }
                .firstOrNull()
                ?.apply {
                    putEventRecordInDatabase(
                        record.copy(
                            data = updateActiveTimeData(record.data, time),
                            spentTime = time
                        )
                    )
                }
        } catch (e: Exception) {
            Timber.e(e, "Error at tracking time")
        }
    }

    private fun EventConverter.buildEventRecord(e: Event): EventRecord? =
        getEvents(
            "$NOT_SENT_FILTER AND ${EventRecord.MERGE_KEY} = ? AND ${EventRecord.TIME} > ?",
            arrayOf(e.mergeKey.toString(), (System.currentTimeMillis() - configuration.eventsMergePeriod).toString()),
            limit = "1"
        ).firstOrNull()?.let {
            update(it, e)
        } ?: toEventRecord(e)

    private fun ContentValues.toEventRecord(): EventRecord =
        EventRecord(
            getAsString(EventRecord.TYPE),
            getAsString(EventRecord.CUSTOM_ID),
            getAsString(EventRecord.DATA),
            getAsString(EventRecord.CKP),
            getAsString(EventRecord.RND),
            getAsLong(EventRecord.TIME),
            getAsLong(EventRecord.SPENT_TIME),
            getAsInteger(EventRecord.MERGE_KEY),
            getAsLong(BaseColumns._ID),
            getAsBoolean(EventRecord.IS_SENT)
        )

    companion object {
        private const val NOT_SENT_FILTER = "${EventRecord.IS_SENT} = 0"
    }
}
