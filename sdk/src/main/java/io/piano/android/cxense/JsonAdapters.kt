package io.piano.android.cxense

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonQualifier
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson
import io.piano.android.cxense.model.EventDataRequest
import io.piano.android.cxense.model.WidgetItem
import okio.Buffer

@Retention(AnnotationRetention.RUNTIME)
@JsonQualifier
annotation class IntString

@Retention(AnnotationRetention.RUNTIME)
@JsonQualifier
annotation class DoubleString

internal object IntStringAdapter {
    @FromJson
    @IntString
    fun fromJson(value: String): Int = value.toInt()

    @ToJson
    fun toJson(@IntString value: Int): String = value.toString()
}

internal object DoubleStringAdapter {
    @FromJson
    @DoubleString
    fun fromJson(value: String): Double = value.toDouble()

    @ToJson
    fun toJson(@DoubleString value: Double): String = "%.2f".format(value)
}

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

object WidgetItemAdapter {
    @FromJson
    fun fromJson(props: Map<String, Any>): WidgetItem =
        WidgetItem(
            props[TITLE]?.toString(),
            props[URL]?.toString(),
            props[CLICK_URL]?.toString(),
            props.filterKeys { it !in listOf(TITLE, URL, CLICK_URL) }
        )

    @Suppress("UNUSED_PARAMETER")
    @ToJson
    fun toJson(value: WidgetItem): Map<String, Any> {
        TODO("Not supported")
    }

    private const val TITLE = "title"
    private const val URL = "url"
    private const val CLICK_URL = "click_url"
}
