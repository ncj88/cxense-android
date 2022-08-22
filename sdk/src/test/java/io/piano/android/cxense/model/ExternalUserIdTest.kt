package io.piano.android.cxense.model

import io.piano.android.cxense.assertFailsWithMessage
import kotlin.test.Test

class ExternalUserIdTest {
    @Test
    fun createWithInternalId() {
        ExternalUserId("cx", "test")
    }

    @Test
    fun createWithInternalIdError() {
        assertFailsWithMessage<IllegalArgumentException>(
            "characters for internal ids",
            "Expected fail for internal user id"
        ) {
            ExternalUserId("cx", "test/")
        }
    }

    @Test
    fun createWithExternalId() {
        ExternalUserId("cxd", "test/")
    }

    @Test
    fun createWithExternalIdError() {
        assertFailsWithMessage<IllegalArgumentException>(
            "characters for external ids",
            "Expected fail for external user id"
        ) {
            ExternalUserId("cxd", "test'")
        }
    }
}
