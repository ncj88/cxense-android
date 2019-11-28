package com.cxense.cxensesdk.model

import com.cxense.cxensesdk.assertFailsWithMessage
import com.nhaarman.mockitokotlin2.mock
import kotlin.test.Test

class UserSegmentRequestTest {
    private val identity: UserIdentity = mock()

    @Test
    fun buildValid() {
        UserSegmentRequest(listOf(identity), listOf("sitegroupId"))
    }

    @Test
    fun buildWithoutIdentities() {
        assertFailsWithMessage<IllegalArgumentException>("at least one user identity", "Expected fail for identities") {
            UserSegmentRequest(listOf(), listOf("sitegroupId"))
        }
    }

    @Test
    fun buildWithoutSitegroups() {
        assertFailsWithMessage<IllegalArgumentException>(
            "at least one not empty site group id",
            "Expected fail for sitegroups"
        ) {
            UserSegmentRequest(listOf(identity), listOf(""))
        }
    }
}
