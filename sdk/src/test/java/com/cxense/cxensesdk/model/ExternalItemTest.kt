package com.cxense.cxensesdk.model

import com.cxense.cxensesdk.assertFailsWithMessage
import kotlin.test.Test

class ExternalItemTest {
    @Test
    fun withInvalidGroup() {
        assertFailsWithMessage<IllegalArgumentException>("Group should not be empty", "Expected fail for group") {
            ExternalItem("", "")
        }
    }

    @Test
    fun withInvalidItem() {
        assertFailsWithMessage<IllegalArgumentException>("Item can't be longer", "Expected fail for item") {
            ExternalItem("group", "q".repeat(500))
        }
    }

    @Test
    fun createValid() {
        ExternalItem("group", "item")
    }
}
