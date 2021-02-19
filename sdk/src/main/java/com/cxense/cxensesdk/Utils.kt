package com.cxense.cxensesdk

import android.content.ContentValues
import android.provider.BaseColumns
import com.cxense.cxensesdk.db.EventRecord
import com.cxense.cxensesdk.model.EventStatus

@Suppress("NOTHING_TO_INLINE", "UNCHECKED_CAST")
internal inline fun <T, R> Sequence<Pair<T, R?>>.filterNotNullValues(): Sequence<Pair<T, R>> =
    filterNot { it.second == null } as Sequence<Pair<T, R>>

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
