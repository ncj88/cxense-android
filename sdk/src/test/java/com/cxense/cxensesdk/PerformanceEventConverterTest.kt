package com.cxense.cxensesdk

import com.cxense.cxensesdk.db.EventRecord
import com.cxense.cxensesdk.model.PerformanceEvent
import com.google.gson.Gson
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
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
    private val gson: Gson = mock {
        on { fromJson<PerformanceEvent>(any<String>(), any<Class<PerformanceEvent>>()) } doReturn event
        on { toJson(any<PerformanceEvent>()) } doReturn "{}"
    }
    private val converter = PerformanceEventConverter(gson)

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
        assertNotNull(converter.extractQueryData(EventRecord("type", "", "{}")))
    }

    @Test
    fun toEventRecord() {
        assertNotNull(converter.toEventRecord(event))
        verify(gson).toJson(any<PerformanceEvent>())
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
