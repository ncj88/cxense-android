package com.cxense.cxensesdk.model;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-07-03).
 */

public abstract class Event {
    String eventId;

    Event(String eventId) {
        this.eventId = eventId;
    }

    /**
     * Gets custom event id, that used for tracking locally.
     *
     * @return event id
     */
    public String getEventId() {
        return eventId;
    }
}
