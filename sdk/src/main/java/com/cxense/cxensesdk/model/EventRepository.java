package com.cxense.cxensesdk.model;

import android.content.ContentValues;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cxense.cxensesdk.EventConverter;
import com.cxense.cxensesdk.EventStatus;
import com.cxense.cxensesdk.PageViewEventConverter;
import com.cxense.cxensesdk.db.DatabaseHelper;
import com.cxense.cxensesdk.db.EventRecord;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2018-09-17).
 */
public class EventRepository {
    private static final String TAG = EventRepository.class.getSimpleName();
    private final DatabaseHelper databaseHelper;
    private final ObjectMapper mapper;
    private final List<EventConverter> eventConverters;

    public EventRepository(@NonNull DatabaseHelper databaseHelper, @NonNull ObjectMapper mapper, @NonNull List<EventConverter> eventConverters) {
        this.databaseHelper = databaseHelper;
        this.mapper = mapper;
        this.eventConverters = eventConverters;
    }

    public void putEventsInDatabase(@NonNull Event... events) {
        for (Event event : events) {
            try {
                for (EventConverter converter : eventConverters) {
                    if (converter.canConvert(event)) {
                        putEventRecordInDatabase(converter.toEventRecord(event));
                        break;
                    }
                }
            } catch (JsonProcessingException e) {
                // TODO: May be we need to rethrow new exception?
                Log.e(TAG, "Can't serialize event data", e);
            } catch (Exception e) {
                Log.e(TAG, "Error at pushing event", e);
            }
        }
    }

    public long putEventRecordInDatabase(@NonNull EventRecord record) {
        return databaseHelper.save(record);
    }

    public void deleteOutdatedEvents(long outdatePeriod) {
        databaseHelper.delete(EventRecord.TABLE_NAME, EventRecord.TIME + " < ?",
                new String[]{"" + (System.currentTimeMillis() - outdatePeriod)});
    }

    @NonNull
    public List<EventRecord> getNotSubmittedPvEvents() {
        return getEvents(EventRecord.IS_SENT + " = 0 AND " + EventRecord.EVENT_TYPE + " = ?", PageViewEvent.DEFAULT_EVENT_TYPE);
    }

    @NonNull
    public List<EventRecord> getNotSubmittedDmpEvents() {
        return getEvents(EventRecord.IS_SENT + " = 0 AND " + EventRecord.EVENT_TYPE + " <> ? AND " + EventRecord.EVENT_TYPE + " <> ?", PageViewEvent.DEFAULT_EVENT_TYPE, ConversionEvent.EVENT_TYPE);
    }

    @NonNull
    public List<EventRecord> getNotSubmittedConversionEvents() {
        return getEvents(EventRecord.IS_SENT + " = 0 AND " + EventRecord.EVENT_TYPE + " = ?", ConversionEvent.EVENT_TYPE);
    }

    @NonNull
    private List<EventRecord> getEvents(String selection, String... selectionArgs) {
        List<ContentValues> values = databaseHelper.query(EventRecord.TABLE_NAME, EventRecord.COLUMNS,
                selection, selectionArgs, null, null, EventRecord.TIME + " ASC");
        List<EventRecord> records = new ArrayList<>();
        for (ContentValues cv : values) {
            records.add(new EventRecord(cv));
        }
        return records;
    }

    @Nullable
    public EventRecord getPvEventFromDatabase(String eventId) {
        List<ContentValues> values = databaseHelper.query(EventRecord.TABLE_NAME, EventRecord.COLUMNS,
                EventRecord.EVENT_CUSTOM_ID + "= ? AND " + EventRecord.EVENT_TYPE + "= ?",
                new String[]{eventId, PageViewEvent.DEFAULT_EVENT_TYPE}, null,
                null, EventRecord.TIME + " DESC");
        if (values.isEmpty())
            return null;
        return new EventRecord(values.get(0));
    }

    @NonNull
    public List<EventStatus> getEventStatuses() {
        List<ContentValues> values = databaseHelper.query(EventRecord.TABLE_NAME,
                new String[]{EventRecord.EVENT_CUSTOM_ID, EventRecord.IS_SENT}, null,
                null, null, null, EventRecord.TIME + " ASC");
        List<EventStatus> statuses = new ArrayList<>();
        for (ContentValues cv : values) {
            EventRecord record = new EventRecord(cv);
            statuses.add(new EventStatus(record.customId, record.isSent));
        }
        return statuses;
    }

    public void putEventTime(String eventId, long activeTime) {
        try {
            EventRecord record = getPvEventFromDatabase(eventId);
            // Only for page view events
            if (record == null)
                return;
            EventRecord newRecord = new EventRecord(record);
            if (activeTime == 0)
                activeTime = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - newRecord.timestamp);
            newRecord.spentTime = activeTime;

            // some black magic with map
            Map<String, String> eventMap = mapper.readValue(newRecord.data, new TypeReference<HashMap<String, String>>() {
            });
            eventMap.put(PageViewEventConverter.ACTIVE_RND, eventMap.get(PageViewEventConverter.RND));
            eventMap.put(PageViewEventConverter.ACTIVE_TIME, eventMap.get(PageViewEventConverter.TIME));
            eventMap.put(PageViewEventConverter.ACTIVE_SPENT_TIME, "" + activeTime);
            newRecord.data = mapper.writeValueAsString(eventMap);

            putEventRecordInDatabase(newRecord);
        } catch (JsonProcessingException e) {
            Log.e(TAG, "Can't serialize event data", e);
        } catch (IOException e) {
            // TODO: May be we need to rethrow new exception?
            Log.e(TAG, "Can't deserialize event data", e);
        } catch (Exception e) {
            Log.e(TAG, "Error at tracking time", e);
        }
    }
}
