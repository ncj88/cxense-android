package com.cxense.cxensesdk.model;

import androidx.annotation.Nullable;

import com.cxense.cxensesdk.RawJsonAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Request object for pushing events to server.
 *
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-05).
 */

public final class EventDataRequest {
    @SerializedName("events")
    @JsonAdapter(RawJsonAdapter.class)
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
