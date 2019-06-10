package com.cxense.cxensesdk;

import androidx.annotation.NonNull;

import java.util.List;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2018-09-18).
 */
public interface DispatchEventsCallback {
    void onSend(@NonNull List<EventStatus> statuses);
}
