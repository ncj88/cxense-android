package com.cxense.cxensesdk;

import androidx.annotation.NonNull;

import com.cxense.cxensesdk.db.EventRecord;
import com.cxense.cxensesdk.model.ConversionEvent;
import com.cxense.cxensesdk.model.Event;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

class ConversionEventConverter extends EventConverter<ConversionEvent> {
    private final ObjectMapper mapper;

    ConversionEventConverter(ObjectMapper mapper) {
        this.mapper = mapper;
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
    public EventRecord toEventRecord(@NonNull ConversionEvent event) throws JsonProcessingException {
        EventRecord record = new EventRecord();
        record.customId = event.getEventId();
        record.data = mapper.writeValueAsString(event);
        record.timestamp = System.currentTimeMillis();
        record.eventType = event.getType();
        return record;
    }
}
