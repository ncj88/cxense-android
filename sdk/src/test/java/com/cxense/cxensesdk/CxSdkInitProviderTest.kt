package com.cxense.cxensesdk

import android.content.Context
import android.content.pm.ProviderInfo
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doNothing
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import kotlin.test.Test

class CxSdkInitProviderTest {
    private val context: Context = mock()
    private val provider = spy(CxSdkInitProvider())

    @Test
    fun onCreate() {
        doReturn(context).`when`(provider).context
        doNothing().`when`(provider).initCxense(any())
        provider.onCreate()
        verify(provider).context
        verify(provider).initCxense(eq(context))
    }

    @Test
    fun checkAttachInfo() {
        provider.checkAttachInfo(ProviderInfo())
    }

    @Test
    fun checkAttachInfoInvalid() {
        assertFailsWithMessage<IllegalStateException>(
            "Incorrect provider authority in manifest",
            "Expected fail for attach info"
        ) {
            provider.checkAttachInfo(
                ProviderInfo().apply {
                    authority = BuildConfig.AUTHORITY
                }
            )
        }
    }
}
