package com.cxense.cxensesdk

import com.cxense.cxensesdk.db.DatabaseHelper
import com.cxense.cxensesdk.db.EventRecord
import com.cxense.cxensesdk.model.ConversionEvent
import com.cxense.cxensesdk.model.PageViewEvent
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class EventRepositoryTest {
    private val databaseHelper: DatabaseHelper = mock()
    private val converter: PageViewEventConverter = mock {
        on { updateActiveTimeData(any(), any()) } doReturn "{}"
    }
    private val eventConverters = listOf(converter)

    private lateinit var repository: EventRepository

    @BeforeTest
    fun setUp() {
        repository = spy(EventRepository(databaseHelper, eventConverters))
    }

    @Test
    fun putEventsInDatabase() {
        whenever(converter.canConvert(any())).thenReturn(true)
        whenever(converter.toEventRecord(any())).thenReturn(mock())
        doReturn(0L).`when`(repository).putEventRecordInDatabase(any())
        repository.putEventsInDatabase(arrayOf(mock()))
        verify(converter).canConvert(any())
        verify(converter).toEventRecord(any())
        verify(repository).putEventRecordInDatabase(any())
    }

    @Test
    fun putEventRecordInDatabase() {
        repository.putEventRecordInDatabase(mock())
        verify(databaseHelper).save(any())
    }

    @Test
    fun deleteOutdatedEvents() {
        repository.deleteOutdatedEvents(1)
        verify(databaseHelper).delete(any(), any())
    }

    @Test
    fun getNotSubmittedPvEvents() {
        doReturn(listOf<EventRecord>(mock())).`when`(repository).getEvents(any(), any())
        assertEquals(1, repository.getNotSubmittedPvEvents().size)
        verify(repository).getEvents(any(), eq(PageViewEvent.EVENT_TYPE))
    }

    @Test
    fun getNotSubmittedDmpEvents() {
        doReturn(listOf<EventRecord>(mock())).`when`(repository).getEvents(any(), any(), any())
        assertEquals(1, repository.getNotSubmittedDmpEvents().size)
        verify(repository).getEvents(any(), eq(PageViewEvent.EVENT_TYPE), eq(ConversionEvent.EVENT_TYPE))
    }

    @Test
    fun getNotSubmittedConversionEvents() {
        doReturn(listOf<EventRecord>(mock())).`when`(repository).getEvents(any(), any())
        assertEquals(1, repository.getNotSubmittedConversionEvents().size)
        verify(repository).getEvents(any(), eq(ConversionEvent.EVENT_TYPE))
    }

    @Test
    fun getEvents() {
        repository.getEvents(any())
        verify(databaseHelper).query(any(), anyOrNull(), any(), anyOrNull(), anyOrNull(), any(), anyOrNull())
    }

    @Test
    fun getPvEventFromDatabase() {
        repository.getPvEventFromDatabase("")
        verify(databaseHelper).query(any(), anyOrNull(), any(), anyOrNull(), anyOrNull(), any(), anyOrNull())
    }

    @Test
    fun getEventStatuses() {
        repository.getEventStatuses()
        verify(databaseHelper).query(any(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), any(), anyOrNull())
    }

    @Test
    fun putEventTime() {
        val record = EventRecord("type", null, "")
        doReturn(record).`when`(repository).getPvEventFromDatabase(any())
        repository.putEventTime("", 0)
        verify(repository).getPvEventFromDatabase(any())
        verify(converter).updateActiveTimeData(any(), any())
        verify(repository).putEventRecordInDatabase(any())
    }

    @Test
    fun putEventTimeNoRecord() {
        doReturn(null).`when`(repository).getPvEventFromDatabase(any())
        repository.putEventTime("", 0)
        verify(repository).getPvEventFromDatabase(any())
        verify(repository, never()).putEventRecordInDatabase(any())
    }
}
