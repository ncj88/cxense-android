package com.cxense.cxensesdk

import android.content.ContentValues
import android.provider.BaseColumns
import com.cxense.cxensesdk.db.EventRecord
import com.cxense.cxensesdk.model.EventStatus
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken

typealias DispatchEventsCallback = (List<EventStatus>) -> Unit

fun <T : Any> JsonReader.readList(nextFunction: (JsonReader) -> T): List<T> {
    beginArray()
    return generateSequence { if (peek() != JsonToken.END_ARRAY) nextFunction(this) else null }
        .toList()
        .also {
            endArray()
        }
}

fun ContentValues.toEventRecord(): EventRecord =
    EventRecord(
        getAsString(EventRecord.TYPE),
        getAsString(EventRecord.CUSTOM_ID),
        getAsString(EventRecord.DATA),
        getAsString(EventRecord.CKP),
        getAsString(EventRecord.RND),
        getAsLong(EventRecord.TIME),
        getAsLong(EventRecord.SPENT_TIME)
    ).apply {
        id = getAsLong(BaseColumns._ID)
        isSent = getAsBoolean(EventRecord.IS_SENT)
    }

fun ContentValues.toEventStatus(): EventStatus =
    EventStatus(
        getAsString(EventRecord.CUSTOM_ID),
        getAsBoolean(EventRecord.IS_SENT)
    )
