package io.piano.android.cxense.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doNothing
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.stub
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import org.mockito.ArgumentMatchers.intThat
import org.mockito.ArgumentMatchers.startsWith
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class DatabaseHelperTest {
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var eventRecord: EventRecord

    private val context: Context = mock()
    private val cursor: Cursor = mock {
        on { moveToNext() } doReturn false
    }
    private val db: SQLiteDatabase = mock {
        on {
            query(any(), any(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), any(), anyOrNull())
        } doReturn cursor
    }
    private val contentValues: ContentValues = mock()

    @BeforeTest
    fun setUp() {
        databaseHelper = spy(DatabaseHelper(context))
        doReturn(db).`when`(databaseHelper).writableDatabase
        doReturn(db).`when`(databaseHelper).readableDatabase

        eventRecord = mock {
            on { toContentValues() } doReturn contentValues
        }
    }

    @Test
    fun onCreate() {
        doNothing().`when`(databaseHelper).migrate(any(), any())
        databaseHelper.onCreate(db)
        verify(databaseHelper, times(DatabaseHelper.DATABASE_VERSION)).migrate(eq(db), any())
    }

    @Test
    fun onUpgrade() {
        val oldVersion = 3
        val newVersion = 5
        doNothing().`when`(databaseHelper).migrate(any(), any())
        databaseHelper.onUpgrade(db, oldVersion, newVersion)
        verify(databaseHelper, times(newVersion - oldVersion))
            .migrate(
                eq(db),
                intThat { it in oldVersion until newVersion }
            )
    }

    @Test
    fun migrate() {
        databaseHelper.migrate(db, 0)
        verify(db).execSQL(startsWith("CREATE TABLE "))
    }

    @Test
    fun save() {
        databaseHelper.save(eventRecord)
        verify(eventRecord).toContentValues()
        verify(db).update(any(), eq(contentValues), eq("${BaseColumns._ID} = ?"), any())
    }

    @Test
    fun saveNewRecord() {
        eventRecord.stub {
            on { id } doReturn null
        }
        databaseHelper.save(eventRecord)
        verify(eventRecord).toContentValues()
        verify(db).insert(any(), eq(null), eq(contentValues))
    }

    @Test
    fun deleteRecord() {
        doReturn(1).`when`(databaseHelper).delete(any(), any())
        assertEquals(1, databaseHelper.delete(eventRecord))
        verify(databaseHelper).delete(eq("${BaseColumns._ID} = ?"), any())
    }

    @Test
    fun deleteNotExistRecord() {
        eventRecord.stub {
            on { id } doReturn null
        }
        assertEquals(-1, databaseHelper.delete(eventRecord))
    }

    @Test
    fun deleteFullArgs() {
        assertEquals(0, databaseHelper.delete(null))
        verify(db).delete(any(), eq(null), eq(emptyArray()))
    }

    @Test
    fun query() {
        databaseHelper.query()
        verify(db).query(any(), any(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull())
    }
}
