package com.cxense.cxensesdk.model;

import android.content.ContentValues;

import com.cxense.cxensesdk.BaseTest;
import com.cxense.cxensesdk.EventConverter;
import com.cxense.cxensesdk.db.DatabaseHelper;
import com.cxense.cxensesdk.db.EventRecord;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2018-10-17).
 */
@PrepareForTest({EventRepository.class, EventRecord.class})
public class EventRepositoryTest extends BaseTest {
    private DatabaseHelper databaseHelper;
    private ObjectMapper mapper;
    private EventConverter eventConverter;
    private EventRepository eventRepository;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        whenNew(EventRecord.class).withAnyArguments().thenReturn(mock(EventRecord.class));
        databaseHelper = mock(DatabaseHelper.class);
        mapper = mock(ObjectMapper.class);
        eventConverter = mock(EventConverter.class);
        eventRepository = spy(new EventRepository(databaseHelper, mapper,
                Collections.singletonList(eventConverter)));
    }

    private void prepareDatabaseHelper(List<ContentValues> result) {
        when(databaseHelper.query(eq(EventRecord.TABLE_NAME), eq(EventRecord.COLUMNS), anyString(),
                any(String[].class), isNull(), isNull(), anyString()))
                .thenReturn(result);
    }

    private void verifyDatabaseHelper() {
        verify(databaseHelper).query(eq(EventRecord.TABLE_NAME), eq(EventRecord.COLUMNS), anyString(),
                any(String[].class), isNull(), isNull(), anyString());
    }

    @Test
    public void putEventsInDatabase() throws Exception {
        when(eventConverter.canConvert(any(Event.class))).thenReturn(true);
        when(eventConverter.toEventRecord(any(Event.class))).thenReturn(mock(EventRecord.class));
        when(eventRepository.putEventRecordInDatabase(any(EventRecord.class))).thenReturn(0L);
        eventRepository.putEventsInDatabase(mock(Event.class));
        verify(eventRepository).putEventRecordInDatabase(any(EventRecord.class));
    }

    @Test
    public void putEventRecordInDatabase() {
        EventRecord record = mock(EventRecord.class);
        eventRepository.putEventRecordInDatabase(record);
        verify(databaseHelper).save(record);
    }

    @Test
    public void deleteOutdatedEvents() {
        eventRepository.deleteOutdatedEvents(1234);
        verify(databaseHelper).delete(eq(EventRecord.TABLE_NAME), anyString(), any(String[].class));
    }

    @Test
    public void getNotSubmittedPvEvents() {
        prepareDatabaseHelper(Collections.singletonList(new ContentValues()));
        assertFalse(eventRepository.getNotSubmittedPvEvents().isEmpty());
        verifyDatabaseHelper();
    }

    @Test
    public void getNotSubmittedDmpEvents() {
        prepareDatabaseHelper(Collections.singletonList(new ContentValues()));
        assertFalse(eventRepository.getNotSubmittedDmpEvents().isEmpty());
        verifyDatabaseHelper();
    }

    @Test
    public void getPvEventFromDatabase() {
        prepareDatabaseHelper(Collections.singletonList(new ContentValues()));
        assertNotNull(eventRepository.getPvEventFromDatabase("id"));
        verifyDatabaseHelper();
    }

    @Test
    public void getEventFromDatabaseEmpty() throws Exception {
        prepareDatabaseHelper(Collections.emptyList());
        assertNull(eventRepository.getPvEventFromDatabase("id"));
        verifyDatabaseHelper();
    }

    @Test
    public void getEventStatuses() {
        prepareDatabaseHelper(Collections.emptyList());
        assertTrue(eventRepository.getEventStatuses().isEmpty());
        verify(databaseHelper).query(eq(EventRecord.TABLE_NAME), any(String[].class), isNull(),
                isNull(), isNull(), isNull(), anyString());
    }

    @Test
    public void putEventTime() throws Exception {
        EventRecord record = new EventRecord();
        record.data = "{}";
        doReturn(record).when(eventRepository).getPvEventFromDatabase(anyString());
        when(mapper.readValue(anyString(), any(TypeReference.class))).thenReturn(new HashMap<String, String>());
        when(mapper.writeValueAsString(any(Map.class))).thenReturn("");
        when(eventRepository.putEventRecordInDatabase(any(EventRecord.class))).thenReturn(0L);
        eventRepository.putEventTime("id", 0);
        verify(eventRepository).getPvEventFromDatabase("id");
        verify(mapper).readValue(anyString(), any(TypeReference.class));
        verify(mapper).writeValueAsString(any(Map.class));
        verify(eventRepository).putEventRecordInDatabase(any(EventRecord.class));
    }
}