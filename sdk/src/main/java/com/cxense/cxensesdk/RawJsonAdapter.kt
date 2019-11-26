package com.cxense.cxensesdk

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

class RawJsonAdapter : TypeAdapter<List<String>>() {
    override fun write(output: JsonWriter, value: List<String>?) {
        with(output) {
            beginArray()
            value?.forEach {
                jsonValue(it)
            }
            endArray()
        }
    }

    override fun read(input: JsonReader): List<String> {
        // not optimized version
        return input.readList { it.nextString() }
    }
}