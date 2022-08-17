package io.piano.android.cxense.model

import io.piano.android.cxense.assertFailsWithMessage
import kotlin.test.Test

class UserIdentityTest {
    @Test
    fun buildCustomerIdentity() {
        UserIdentity("abc", "id")
    }

    @Test
    fun buildInternalIdentity() {
        UserIdentity("cx", "id")
    }

    @Test
    fun buildInvalidIdentity() {
        assertFailsWithMessage<IllegalArgumentException>("Type should be", "Expected fail for type") {
            UserIdentity("aa", "id")
        }
    }
}
