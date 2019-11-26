package com.cxense.cxensesdk

import com.cxense.cxensesdk.model.WidgetItem
import com.google.gson.JsonElement
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter

class WidgetItemTypeAdapter(
    private val jsonElementTypeAdapter: TypeAdapter<JsonElement>
) : TypeAdapter<WidgetItem>() {
    override fun write(output: JsonWriter, value: WidgetItem) {
        with(output) {
            beginObject()
                .name(TITLE)
                .value(value.title)
                .name(URL)
                .value(value.url)
                .name(CLICK_URL)
                .value(value.clickUrl)
                .apply {
                    value.properties.forEach { (k, v) ->
                        name(k).jsonValue(v.toString())
                    }
                }
                .endObject()
        }
    }

    override fun read(input: JsonReader): WidgetItem =
        with(input) {
            beginObject()
            val properties = generateSequence { if (hasNext()) nextString() to readValue() else null }
                .toMap(mutableMapOf())
            endObject()
            WidgetItem(
                properties.remove(TITLE)?.toString(),
                properties.remove(URL)?.toString(),
                properties.remove(CLICK_URL)?.toString(),
                properties
            )
        }

    private fun JsonReader.readValue(): Any {
        return when (peek()) {
            JsonToken.BEGIN_ARRAY -> readList { it.nextString() }
            JsonToken.BEGIN_OBJECT -> jsonElementTypeAdapter.read(this)
            else -> nextString()
        }
    }

    companion object {
        private const val TITLE = "title"
        private const val URL = "url"
        private const val CLICK_URL = "click_url"
    }
}
