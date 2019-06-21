package com.cxense.cxensesdk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

import com.cxense.cxensesdk.db.EventRecord;
import com.cxense.cxensesdk.model.Event;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Map;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2018-09-19).
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public abstract class EventConverter<T extends Event> {
    @NonNull
    String escapeString(@Nullable String str) {
        return str != null ? str : "";
    }

    public abstract boolean canConvert(@NonNull Event event);

    @Nullable
    public abstract Map<String, String> toQueryMap(@NonNull T event);

    @NonNull
    public abstract EventRecord toEventRecord(@NonNull T event) throws JsonProcessingException;
}
