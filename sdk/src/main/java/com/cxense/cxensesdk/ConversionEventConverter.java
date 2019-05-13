package com.cxense.cxensesdk;

import com.cxense.cxensesdk.db.EventRecord;
import com.cxense.cxensesdk.model.ConversionEvent;
import com.cxense.cxensesdk.model.Event;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class ConversionEventConverter extends EventConverter<ConversionEvent> {
    private final ObjectMapper mapper;

    public ConversionEventConverter(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public boolean canConvert(Event event) {
        return event instanceof ConversionEvent;
    }

    @Override
    public Map<String, String> toQueryMap(ConversionEvent event) {
        return null;
    }

    @Override
    public EventRecord toEventRecord(ConversionEvent event) throws JsonProcessingException {
        EventRecord record = new EventRecord();
        record.customId = event.getEventId();
        record.data = mapper.writeValueAsString(event);
        record.timestamp = System.currentTimeMillis();
        record.eventType = event.getType();
        return record;
    }
}
