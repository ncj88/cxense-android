package io.piano.android.cxense.model

import io.piano.android.cxense.UserProvider
import io.piano.android.cxense.assertFailsWithMessage
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import kotlin.test.BeforeTest
import kotlin.test.Test

class PerformanceEventBuilderTest {
    private lateinit var builder: PerformanceEvent.Builder

    private val userProvider: UserProvider = mock {
        on { userId } doReturn "userId"
    }

    private val identity: UserIdentity = mock()

    @BeforeTest
    fun setUp() {
        builder = PerformanceEvent.Builder(userProvider, "siteId", "xyz-origin", "eventType", mutableListOf(identity))
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
