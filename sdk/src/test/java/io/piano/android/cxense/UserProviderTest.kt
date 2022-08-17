package io.piano.android.cxense

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class UserProviderTest {
    private val advertisingIdProvider: AdvertisingIdProvider = mock()
    private val provider = UserProvider(advertisingIdProvider)

    @Test
    fun getUserId() {
        whenever(advertisingIdProvider.defaultUserId).thenReturn(USER_ID)
        assertEquals(USER_ID, provider.userId)
        assertEquals(USER_ID, provider.userId)
        verify(advertisingIdProvider, times(1)).defaultUserId
    }

    @Test
    fun getUserIdNotAaid() {
        whenever(advertisingIdProvider.defaultUserId).thenReturn(null)
        assertNotNull(UUID.fromString(provider.userId))
    }

    @Test
    fun setInvalidUserId() {
        assertFailsWithMessage<IllegalArgumentException>("user id must be", "Expected fail for user id") {
            provider.userId = "1"
        }
    }

    companion object {
        private const val USER_ID = "test user id"
    }
}
