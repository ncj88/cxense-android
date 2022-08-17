package io.piano.android.cxense

import io.piano.android.cxense.db.EventRecord
import io.piano.android.cxense.model.PerformanceEvent
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.squareup.moshi.JsonAdapter
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PerformanceEventConverterTest {
    private val event: PerformanceEvent = mock {
        on { eventType } doReturn ""
    }
    private val jsonAdapter: JsonAdapter<PerformanceEvent> = mock {
        on { fromJson(any<String>()) } doReturn event
        on { toJson(any()) } doReturn "{}"
    }
    private val converter = PerformanceEventConverter(jsonAdapter)

    @Test
    fun canConvertConversionEvent() {
        assertTrue {
            converter.canConvert(event)
        }
    }

    @Test
    fun canConvertOtherEvent() {
        assertFalse {
            converter.canConvert(mock())
        }
    }

    @Test
    fun extractQueryData() {
        assertNotNull(converter.extractQueryData(EventRecord("type", "", "{}", mergeKey = 0)))
    }

    @Test
    fun toEventRecord() {
        assertNotNull(converter.toEventRecord(event))
        verify(jsonAdapter).toJson(any())
    }

    @Test
    fun toEventRecordNotPerfEvent() {
        assertNull(converter.toEventRecord(mock()))
    }

    @Test
    fun prepareKey() {
        assertEquals(COMPOUND_KEY, converter.prepareKey(OBJECT_NAME, NAME_KEY, VALUE_KEY, NAME))
    }

    companion object {
        private const val OBJECT_NAME = "object"
        private const val NAME_KEY = "keyName"
        private const val VALUE_KEY = "keyValue"
        private const val NAME = "test"
        private const val COMPOUND_KEY = "$OBJECT_NAME/$NAME_KEY:$NAME/$VALUE_KEY"
    }
}
