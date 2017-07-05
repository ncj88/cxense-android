package com.cxense.cxensesdk;

import com.cxense.cxensesdk.db.EventRecord;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-07-03).
 */

public abstract class Event {
    public abstract EventRecord toEventRecord() throws JsonProcessingException;
}
