package com.cxense.cxensesdk

import androidx.annotation.RestrictTo
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

/**
 * Used for writing prepared json as raw string.
 *
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
internal class RawJsonAdapter : TypeAdapter<List<String>>() {
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
