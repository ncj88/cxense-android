package com.cxense.cxensesdk.db

import android.content.ContentValues
import android.provider.BaseColumns
import androidx.annotation.RestrictTo

/**
 * Database class for events
 *
 **/
@RestrictTo(RestrictTo.Scope.LIBRARY)
data class EventRecord(
    val eventType: String,
    val customId: String?,
    val data: String,
    val ckp: String? = null,
    val rnd: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val spentTime: Long? = null
) {
    var id: Long? = null
    var isSent: Boolean = false

    fun toContentValues(): ContentValues =
        ContentValues().apply {
            put(TYPE, eventType)
            put(CUSTOM_ID, customId)
            put(DATA, data)
            put(CKP, ckp)
            put(RND, rnd)
            put(TIME, timestamp)
            put(SPENT_TIME, spentTime)
            put(IS_SENT, isSent)
        }

    companion object {
        internal const val TYPE = "type"
        internal const val CUSTOM_ID = "customId"
        internal const val DATA = "event"
        internal const val CKP = "ckp"
        internal const val RND = "rnd"
        internal const val TIME = "time"
        internal const val SPENT_TIME = "spentTime"
        internal const val IS_SENT = "isSent"

        const val TABLE_NAME = "event"
        @JvmStatic
        val COLUMNS = arrayOf(
            BaseColumns._ID,
            CUSTOM_ID, DATA,
            TIME,
            CKP,
            RND,
            TYPE,
            SPENT_TIME,
            IS_SENT
        )
    }
}
