package com.cxense.cxensesdk;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-11-13).
 */
@SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
public class QueueStatus {
    @NonNull
    public List<String> sentEvents;
    @NonNull
    public List<String> notSentEvents;

    QueueStatus(@NonNull List<EventStatus> eventStatuses) {
        sentEvents = new ArrayList<>();
        notSentEvents = new ArrayList<>();
        for (EventStatus eventStatus : eventStatuses) {
            if (eventStatus.isSent)
                sentEvents.add(eventStatus.eventId);
            else notSentEvents.add(eventStatus.eventId);
        }
        sentEvents = Collections.unmodifiableList(sentEvents);
        notSentEvents = Collections.unmodifiableList(notSentEvents);
    }
}
