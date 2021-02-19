package com.cxense.cxensesdk

import com.cxense.cxensesdk.model.ConversionEvent
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.squareup.moshi.JsonAdapter
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ConversionEventConverterTest {
    private val jsonAdapter: JsonAdapter<ConversionEvent> = mock {
        on { toJson(any()) } doReturn "{}"
    }
    private val converter = ConversionEventConverter(jsonAdapter)
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
        verify(jsonAdapter).toJson(any())
    }

    @Test
    fun toEventRecordOtherEvent() {
        assertNull(converter.toEventRecord(mock()))
    }
}
