package io.piano.android.cxense

import kotlin.test.Test
import kotlin.test.assertNotEquals

class GenerationTest {
    @Test
    fun versionTest() {
        assertNotEquals("unspecified", BuildConfig.SDK_VERSION)
    }
}
