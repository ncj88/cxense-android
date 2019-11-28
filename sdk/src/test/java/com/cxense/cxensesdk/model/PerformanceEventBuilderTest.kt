package com.cxense.cxensesdk.model

import com.cxense.cxensesdk.assertFailsWithMessage
import com.nhaarman.mockitokotlin2.mock
import kotlin.test.BeforeTest
import kotlin.test.Test

class PerformanceEventBuilderTest {
    private lateinit var builder: PerformanceEvent.Builder

    private val identity: UserIdentity = mock()

    @BeforeTest
    fun setUp() {
        builder = PerformanceEvent.Builder("siteId", "xyz-origin", "eventType", mutableListOf(identity))
    }

    @Test
    fun buildWithoutIdentities() {
        assertFailsWithMessage<IllegalStateException>(
            "at least one user identity",
            "Expected fail for user identities"
        ) {
            PerformanceEvent.Builder("", "", "").build()
        }
    }

    @Test
    fun buildEmptySiteId() {
        assertFailsWithMessage<IllegalStateException>("Site id", "Expected fail for site id") {
            builder.siteId("").build()
        }
    }

    @Test
    fun buildEmptyEventType() {
        assertFailsWithMessage<IllegalStateException>("Event type", "Expected fail for event type") {
            builder.eventType("").build()
        }
    }

    @Test
    fun buildInvalidOrigin() {
        assertFailsWithMessage<IllegalStateException>("Origin", "Expected fail for origin") {
            builder.origin("blablabla").build()
        }
    }
}
