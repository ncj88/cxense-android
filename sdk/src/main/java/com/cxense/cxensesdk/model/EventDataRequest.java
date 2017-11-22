package com.cxense.cxensesdk.model;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Request object for pushing events to server.
 *
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-05).
 */

public final class EventDataRequest {
    @JsonProperty("events")
    @JsonRawValue
    List<String> events;

    /**
     * Create new event packet
     *
     * @param events list of json serialized events
     */
    public EventDataRequest(@Nullable List<String> events) {
        this.events = new ArrayList<>();
        if (events != null)
            this.events.addAll(events);
    }
}
