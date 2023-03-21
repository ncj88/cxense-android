package io.piano.android.cxense

import io.piano.android.cxense.db.DatabaseHelper
import io.piano.android.cxense.db.EventRecord
import io.piano.android.cxense.model.ConsentSettings
import io.piano.android.cxense.model.ConversionEvent
import io.piano.android.cxense.model.PageViewEvent
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class EventRepositoryTest {
    private val configuration: CxenseConfiguration = mock {
        on { consentSettings } doReturn ConsentSettings()
    }
    private val databaseHelper: DatabaseHelper = mock()
    private val converter: PageViewEventConverter = mock {
        on { updateActiveTimeData(any(), any()) } doReturn "{}"
    }
    private val eventConverters = listOf(converter)

    private lateinit var repository: EventRepository

    @BeforeTest
    fun setUp() {
        repository = spy(EventRepository(configuration, databaseHelper, eventConverters))
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
        doReturn(listOf<EventRecord>(mock())).`when`(repository).getEvents(any(), any(), anyOrNull())
        assertEquals(1, repository.getNotSubmittedPvEvents().size)
        verify(repository).getEvents(any(), eq(arrayOf(PageViewEvent.EVENT_TYPE)), anyOrNull())
    }

    @Test
    fun getNotSubmittedDmpEvents() {
        doReturn(listOf<EventRecord>(mock())).`when`(repository).getEvents(any(), any(), anyOrNull())
        assertEquals(1, repository.getNotSubmittedDmpEvents().size)
        verify(repository).getEvents(
            any(),
            eq(arrayOf(PageViewEvent.EVENT_TYPE, ConversionEvent.EVENT_TYPE)),
            anyOrNull()
        )
    }

    @Test
    fun getNotSubmittedConversionEvents() {
        doReturn(listOf<EventRecord>(mock())).`when`(repository).getEvents(any(), any(), anyOrNull())
        assertEquals(1, repository.getNotSubmittedConversionEvents().size)
        verify(repository).getEvents(any(), eq(arrayOf(ConversionEvent.EVENT_TYPE)), anyOrNull())
    }

    @Test
    fun getEvents() {
        repository.getEvents(any(), any(), anyOrNull())
        verify(databaseHelper).query(any(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), any(), anyOrNull())
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
        val record = EventRecord("type", null, "", mergeKey = 0)
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
