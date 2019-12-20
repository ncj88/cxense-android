package com.cxense.cxensesdk

import com.cxense.cxensesdk.model.ConversionEvent
import com.cxense.cxensesdk.model.Event
import com.google.gson.Gson
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ConversionEventConverterTest {
    private val gson: Gson = mock {
        on { toJson(any<Event>()) } doReturn "{}"
    }
    private val converter = ConversionEventConverter(gson)
    private val event: ConversionEvent = mock()

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
    fun toEventRecordConversionEvent() {
        assertNotNull(converter.toEventRecord(event))
        verify(gson).toJson(any<Event>())
    }

    @Test
    fun toEventRecordOtherEvent() {
        assertNull(converter.toEventRecord(mock()))
    }
}
