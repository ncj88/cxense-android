package io.piano.android.cxense.model

import io.piano.android.cxense.assertFailsWithMessage
import kotlin.test.BeforeTest
import kotlin.test.Test

class WidgetContextBuilderTest {
    private lateinit var builder: WidgetContext.Builder

    @BeforeTest
    fun setUp() {
        builder = WidgetContext.Builder("http://example.com")
    }

    @Test
    fun buildValid() {
        builder.build()
    }

    @Test
    fun buildInvalidUrl() {
        assertFailsWithMessage<IllegalStateException>("valid url as source", "Expected fail for url") {
            builder.url("test").build()
        }
    }

    @Test
    fun buildInvalidReferrer() {
        assertFailsWithMessage<IllegalStateException>("valid url as referrer", "Expected fail for referrer") {
            builder.referrer("test").build()
        }
    }
}
