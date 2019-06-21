package com.cxense.cxensesdk.db;

import android.content.ContentValues;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Database class for events
 *
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-05).
 */

public final class EventRecord extends DatabaseObject {
    public static final String EVENT_CUSTOM_ID = "customId";
    public static final String EVENT = "event";
    public static final String TIME = "time";
    public static final String CKP = "ckp";
    public static final String RND = "rnd";
    public static final String EVENT_TYPE = "type";
    public static final String SPENT_TIME = "spentTime";
    public static final String IS_SENT = "isSent";

    public static final String TABLE_NAME = "event";
    public static final String[] COLUMNS = {_ID, EVENT_CUSTOM_ID, EVENT, TIME, CKP, RND, EVENT_TYPE, SPENT_TIME,
            IS_SENT};

    /**
     * Contains customer event id
     */
    @Nullable
    public String customId;
    /**
     * Contains the event data which should be used to report
     */
    @Nullable
    public String data;
    /**
     * Contains unix time in UTZ sense the epoch when the event was registered
     */
    public long timestamp;
    /**
     * Contains Cxense site-specific persistent cookie
     */
    @Nullable
    public String ckp;
    /**
     * Contains a random number, for cache-busting purposes and to uniquely identify a page-view request
     */
    @Nullable
    public String rnd;
    /**
     * Contains the event type.
     */
    @Nullable
    public String eventType;
    /**
     * Contains spent time in seconds, null if time not tracked
     */
    @Nullable
    public Long spentTime = null;
    /**
     * Contains sent flag for event
     */
    public boolean isSent = false;

    public EventRecord() {
        super();
    }

    public EventRecord(@NonNull EventRecord other) {
        this();
        customId = other.customId;
        data = other.data;
        timestamp = other.timestamp;
        ckp = other.ckp;
        rnd = other.rnd;
        eventType = other.eventType;
        // We don't want to override original
        id = null;
    }

    public EventRecord(@NonNull ContentValues values) {
        super(values);
        customId = values.getAsString(EVENT_CUSTOM_ID);
        data = values.getAsString(EVENT);
        timestamp = values.getAsLong(TIME);
        ckp = values.getAsString(CKP);
        rnd = values.getAsString(RND);
        eventType = values.getAsString(EVENT_TYPE);
        spentTime = values.getAsLong(SPENT_TIME);
        isSent = values.getAsBoolean(IS_SENT);
    }

    @NonNull
    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @NonNull
    @Override
    protected ContentValues toContentValues() {
        ContentValues values = super.toContentValues();
        values.put(EVENT_CUSTOM_ID, customId);
        values.put(EVENT, data);
        values.put(TIME, timestamp);
        values.put(CKP, ckp);
        values.put(RND, rnd);
        values.put(EVENT_TYPE, eventType);
        values.put(SPENT_TIME, spentTime);
        values.put(IS_SENT, isSent);
        return values;
    }
}
