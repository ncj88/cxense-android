package io.piano.android.cxense

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.util.DisplayMetrics
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.mock
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DeviceInfoProviderTest {
    private val dm: DisplayMetrics = mock()
    private val res: Resources = mock {
        on { displayMetrics } doReturn dm
    }
    private val pm: PackageManager = mock {
        on { getPackageInfo(any<String>(), anyInt()) } doReturn PackageInfo().apply { versionName = VERSION }
        on { getApplicationLabel(any()) } doReturn LABEL
    }
    private val context: Context = mock {
        on { resources } doReturn res
        on { packageName } doReturn ""
        on { packageManager } doReturn pm
        on { applicationInfo } doReturn mock()
    }
    private val provider = DeviceInfoProvider(context)

    @Test
    fun getDisplayMetrics() {
        assertEquals(dm, provider.displayMetrics)
    }

    @Test
    fun getApplicationVersion() {
        assertEquals(VERSION, provider.applicationVersion)
    }

    @Test
    fun getApplicationVersionError() {
        doThrow(PackageManager.NameNotFoundException()).`when`(pm).getPackageInfo(any<String>(), anyInt())
        assertNull(provider.applicationVersion)
    }

    @Test
    fun getApplicationName() {
        assertEquals(LABEL, provider.applicationName)
    }

    companion object {
        private const val VERSION = "test version"
        private const val LABEL = "test label"
    }
}
