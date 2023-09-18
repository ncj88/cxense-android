package io.piano.android.cxense.model

import io.piano.android.cxense.assertFailsWithMessage
import kotlin.test.Test

class ExternalItemTest {
    @Test
    fun withInvalidGroup() {
        assertFailsWithMessage<IllegalArgumentException>("Group should not be empty", "Expected fail for group") {
            ExternalTypedItem("", TypedItem.String(""))
        }
    }

    @Test
    fun createValid() {
        ExternalTypedItem("group", TypedItem.String("item"))
    }
}
