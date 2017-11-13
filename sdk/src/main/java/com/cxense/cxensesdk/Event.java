package com.cxense.cxensesdk;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.cxense.cxensesdk.db.EventRecord;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Map;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-07-03).
 */

public abstract class Event {
    String eventId;

    Event(String eventId) {
        this.eventId = eventId;
    }

    public abstract EventRecord toEventRecord() throws JsonProcessingException;

    abstract Map<String, String> toQueryMap();

    /**
     * Gets custom event id, that used for tracking locally.
     *
     * @return event id
     */
    public String getEventId() {
        return eventId;
    }

    @NonNull
    String escapeString(@Nullable String str) {
        return str != null ? str : "";
    }
}
