package com.cxense.cxensesdk.model;

import androidx.annotation.Nullable;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-07-03).
 */

public abstract class Event {
    transient String eventId;

    Event(@Nullable String eventId) {
        this.eventId = eventId;
    }

    /**
     * Gets custom event id, that used for tracking locally.
     *
     * @return event id
     */
    @Nullable
    public String getEventId() {
        return eventId;
    }
}
