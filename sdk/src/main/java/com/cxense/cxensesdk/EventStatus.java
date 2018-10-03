package com.cxense.cxensesdk;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-11-13).
 */

public class EventStatus {
    public String eventId;
    public boolean isSent;
    public Exception exception;

    public EventStatus(String eventId, boolean isSent) {
        this.eventId = eventId;
        this.isSent = isSent;
    }

    public EventStatus(String eventId, boolean isSent, Exception exception) {
        this(eventId, isSent);
        this.exception = exception;
    }
}
