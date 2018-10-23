package com.cxense.cxensesdk;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.cxense.cxensesdk.db.EventRecord;
import com.cxense.cxensesdk.model.Event;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Map;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2018-09-19).
 */
public abstract class EventConverter<T extends Event> {
    @NonNull
    String escapeString(@Nullable String str) {
        return str != null ? str : "";
    }

    public abstract boolean canConvert(Event event);

    public abstract Map<String, String> toQueryMap(T event);

    public abstract EventRecord toEventRecord(T event) throws JsonProcessingException;
}
