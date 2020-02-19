package com.cxense.cxensesdk.model

import com.cxense.cxensesdk.UserProvider
import com.cxense.cxensesdk.assertFailsWithMessage
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import kotlin.test.BeforeTest
import kotlin.test.Test

class PageViewEventBuilderTest {
    private lateinit var builder: PageViewEvent.Builder

    private val userProvider: UserProvider = mock {
        on { userId } doReturn "userId"
    }

    @BeforeTest
    fun setUp() {
        builder = PageViewEvent.Builder(userProvider, "siteId", "http://example.com")
    }

    @Test
    fun buildWithoutLocationAndContentId() {
        assertFailsWithMessage<IllegalStateException>(
            "location or content id",
            "Expected fail for location and content id"
        ) {
            builder.location(null).contentId(null).build()
        }
    }

    @Test
    fun buildEmptySiteId() {
        assertFailsWithMessage<IllegalStateException>("Site id", "Expected fail for site id") {
            builder.siteId("").build()
        }
    }

    @Test
    fun buildEmptyContentId() {
        assertFailsWithMessage<IllegalStateException>("Content id", "Expected fail for content id") {
            builder.contentId("").build()
        }
    }

    @Test
    fun buildInvalidLocation() {
        assertFailsWithMessage<IllegalStateException>("valid url as location", "Expected fail for location") {
            builder.location("example.com").build()
        }
    }

    @Test
    fun buildInvalidReferrer() {
        assertFailsWithMessage<IllegalStateException>("valid url as referrer", "Expected fail for location") {
            builder.referrer("example.com").build()
        }
    }
}
