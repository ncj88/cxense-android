package com.cxense.cxensesdk;

import androidx.annotation.NonNull;

import com.cxense.cxensesdk.db.EventRecord;
import com.cxense.cxensesdk.model.ConversionEvent;
import com.cxense.cxensesdk.model.Event;
import com.google.gson.Gson;

import java.util.Map;

class ConversionEventConverter extends EventConverter<ConversionEvent> {
    private final Gson gson;

    ConversionEventConverter(Gson gson) {
        this.gson = gson;
    }

    @Override
    public boolean canConvert(@NonNull Event event) {
        return event instanceof ConversionEvent;
    }

    @Override
    public Map<String, String> toQueryMap(@NonNull ConversionEvent event) {
        return null;
    }

    @NonNull
    @Override
    public EventRecord toEventRecord(@NonNull ConversionEvent event) {
        EventRecord record = new EventRecord();
        record.customId = event.getEventId();
        record.data = gson.toJson(event);
        record.timestamp = System.currentTimeMillis();
        record.eventType = event.getType();
        return record;
    }
}
