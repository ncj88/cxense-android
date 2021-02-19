package com.cxense.cxensesdk

import android.content.ContentValues
import com.cxense.cxensesdk.db.DatabaseHelper
import com.cxense.cxensesdk.db.EventRecord
import com.cxense.cxensesdk.model.ConversionEvent
import com.cxense.cxensesdk.model.Event
import com.cxense.cxensesdk.model.EventStatus
import com.cxense.cxensesdk.model.PageViewEvent
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * Repository for saving/getting events via local database.
 */
class EventRepository(
    private val databaseHelper: DatabaseHelper,
    private val eventConverters: List<EventConverter>
) {
    fun putEventsInDatabase(events: Array<out Event>) {
        events.forEach { e ->
            try {
                eventConverters.firstOrNull { it.canConvert(e) }
                    ?.toEventRecord(e)
                    ?.let {
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
        "${EventRecord.IS_SENT} = 0 AND ${EventRecord.TYPE} = ?",
        PageViewEvent.EVENT_TYPE
    )

    fun getNotSubmittedDmpEvents() = getEvents(
        "${EventRecord.IS_SENT} = 0 AND ${EventRecord.TYPE} <> ? AND ${EventRecord.TYPE} <> ?",
        PageViewEvent.EVENT_TYPE,
        ConversionEvent.EVENT_TYPE
    )

    fun getNotSubmittedConversionEvents() = getEvents(
        "${EventRecord.IS_SENT} = 0 AND ${EventRecord.TYPE} = ?",
        ConversionEvent.EVENT_TYPE
    )

    internal fun getEvents(selection: String?, vararg selectionArgs: String): List<EventRecord> =
        databaseHelper.query(
            selection = selection,
            selectionArgs = selectionArgs,
            orderBy = "${EventRecord.TIME} ASC"
        ).map(ContentValues::toEventRecord)

    fun getPvEventFromDatabase(eventId: String): EventRecord? =
        databaseHelper.query(
            selection = "${EventRecord.CUSTOM_ID} = ? AND ${EventRecord.TYPE} = ?",
            selectionArgs = arrayOf(eventId, PageViewEvent.EVENT_TYPE),
            orderBy = "${EventRecord.TIME} DESC"
        ).firstOrNull()?.toEventRecord()

    fun getEventStatuses(): List<EventStatus> =
        databaseHelper.query(
            columns = arrayOf(EventRecord.CUSTOM_ID, EventRecord.IS_SENT),
            orderBy = "${EventRecord.TIME} ASC"
        ).map(ContentValues::toEventStatus)

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
}
