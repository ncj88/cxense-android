package com.cxense.cxensesdk

import android.util.DisplayMetrics
import com.cxense.cxensesdk.db.EventRecord
import com.cxense.cxensesdk.model.PageViewEvent
import com.google.gson.Gson
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import java.lang.reflect.Type
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PageViewEventConverterTest {
    private val gson: Gson = mock {
        on { fromJson<MutableMap<String, String>>(any<String>(), any<Type>()) } doReturn mutableMapOf()
        on { toJson(any<Map<String, String>>()) } doReturn "{}"
    }
    private val configuration: CxenseConfiguration = mock {
        on { autoMetaInfoTrackingEnabled } doReturn true
    }
    private val deviceInfoProvider: DeviceInfoProvider = mock {
        on { displayMetrics } doReturn DisplayMetrics().apply {
            density = 0f
            widthPixels = 1
            heightPixels = 2
        }
    }

    private val converter = PageViewEventConverter(gson, configuration, deviceInfoProvider)
    private val event: PageViewEvent = mock {
        on { userLocation } doReturn mock()
    }
    private val fixUserIdFunc: () -> String = mock()

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
        assertNotNull(converter.extractQueryData(EventRecord("type", "", "{}"), fixUserIdFunc))
        verify(gson).fromJson<MutableMap<String, String>>(any<String>(), any<Type>())
        verify(fixUserIdFunc).invoke()
    }

    @Test
    fun extractQueryDataWithCkp() {
        whenever(gson.fromJson<MutableMap<String, String>>(any<String>(), any<Type>())).thenReturn(
            mutableMapOf(
                PageViewEventConverter.CKP to "123"
            )
        )
        assertNotNull(converter.extractQueryData(EventRecord("type", "", "{}"), fixUserIdFunc))
        verify(gson).fromJson<MutableMap<String, String>>(any<String>(), any<Type>())
        verify(fixUserIdFunc, never()).invoke()
    }

    @Test
    fun toEventRecord() {
        assertNotNull(converter.toEventRecord(event))
        verify(deviceInfoProvider).displayMetrics
        verify(configuration).autoMetaInfoTrackingEnabled
        verify(gson).toJson(any<Map<String, String>>())
    }

    @Test
    fun toEventRecordNotPvEvent() {
        assertNull(converter.toEventRecord(mock()))
    }

    @Test
    fun updateActiveTimeData() {
        assertNotNull(converter.updateActiveTimeData("{}", 0))
        verify(gson).fromJson<MutableMap<String, String>>(any<String>(), any<Type>())
        verify(gson).toJson(any<Map<String, String>>())
    }
}
