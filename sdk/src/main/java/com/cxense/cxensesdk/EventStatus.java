package com.cxense.cxensesdk;

import androidx.annotation.Nullable;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-11-13).
 */

public class EventStatus {
    @Nullable
    public String eventId;
    public boolean isSent;
    @Nullable
    public Exception exception;

    public EventStatus(@Nullable String eventId, boolean isSent) {
        this.eventId = eventId;
        this.isSent = isSent;
    }

    public EventStatus(@Nullable String eventId, boolean isSent, @Nullable Exception exception) {
        this(eventId, isSent);
        this.exception = exception;
    }
}
