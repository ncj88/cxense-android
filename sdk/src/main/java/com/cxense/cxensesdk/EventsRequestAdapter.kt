package com.cxense.cxensesdk

import com.cxense.cxensesdk.model.EventDataRequest
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import okio.Buffer

class EventsRequestAdapter : JsonAdapter<EventDataRequest>() {
    override fun toJson(writer: JsonWriter, value: EventDataRequest?) {
        requireNotNull(value)
        writer.beginObject()
            .name("events")
            .beginArray()
            .apply {
                value.events.forEach {
                    value(Buffer().writeUtf8(it))
                }
            }
            .endArray()
            .endObject()
    }

    override fun fromJson(reader: JsonReader): EventDataRequest? {
        TODO("Not supported")
    }
}
