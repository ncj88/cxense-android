package com.cxense.cxensesdk

import com.cxense.cxensesdk.model.ConsentSettings
import com.cxense.cxensesdk.model.UserIdentity
import com.cxense.cxensesdk.model.WidgetItem
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doNothing
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.squareup.moshi.Moshi
import org.mockito.stubbing.Answer
import retrofit2.Call
import retrofit2.Callback
import java.util.concurrent.ScheduledExecutorService
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

class CxenseSdkTest {
    private val call: Call<Any> = mock()

    private val executor: ScheduledExecutorService = mock()
    private val configuration: CxenseConfiguration = mock {
        on { consentSettings } doReturn ConsentSettings()
    }
    private val advertisingIdProvider: AdvertisingIdProvider = mock {
        on { defaultUserId } doReturn DEFAULT_USER_ID
        on { limitAdTrackingEnabled } doReturn false
    }
    private val userProvider: UserProvider = mock {
        on { userId } doReturn USER_ID
        on { defaultUser } doReturn mock()
    }
    private val cxApi: CxApi = mock<CxApi>(defaultAnswer = Answer { call })
    private val errorParser: ApiErrorParser = mock()
    private val moshi: Moshi = mock()
    private val eventRepository: EventRepository = mock()
    private val sendTask: SendTask = mock()

    private val cxenseSdk = spy(
        CxenseSdk(
            executor,
            configuration,
            advertisingIdProvider,
            userProvider,
            cxApi,
            errorParser,
            moshi,
            eventRepository,
            sendTask
        )
    )

    @Test
    fun getUserId() {
        assertEquals(USER_ID, cxenseSdk.userId)
        verify(userProvider).userId
    }

    @Test
    fun getDefaultUserId() {
        assertEquals(DEFAULT_USER_ID, cxenseSdk.defaultUserId)
        verify(advertisingIdProvider).defaultUserId
    }

    @Test
    fun getLimitAdTrackingEnabled() {
        assertFalse { cxenseSdk.limitAdTrackingEnabled }
        verify(advertisingIdProvider).limitAdTrackingEnabled
    }

    @Test
    fun setDispatchEventsCallback() {
        cxenseSdk.setDispatchEventsCallback(mock())
        verify(sendTask).sendCallback = any()
    }

    @Test
    fun pushEvents() {
        cxenseSdk.pushEvents(mock())
        verify(executor).execute(any())
    }

    @Test
    fun trackActiveTime() {
        cxenseSdk.trackActiveTime("")
        verify(executor).execute(any())
    }

    @Test
    fun flushEventQueue() {
        cxenseSdk.flushEventQueue()
        verify(executor).execute(eq(sendTask))
    }

    @Test
    fun getQueueStatus() {
        assertNotNull(cxenseSdk.queueStatus)
        verify(eventRepository).getEventStatuses()
    }

    @Test
    fun getDefaultContentUser() {
        assertNotNull(cxenseSdk.defaultContentUser)
        verify(userProvider).defaultUser
    }

    @Test
    fun trackClick() {
        val clickUrl = "clickUrl"
        doNothing().`when`(cxenseSdk).trackClick(any<String>(), any())
        cxenseSdk.trackClick(WidgetItem("title", "url", clickUrl, mapOf()), mock())
        verify(cxenseSdk).trackClick(eq(clickUrl), any())
    }

    @Test
    fun trackClickNullUrl() {
        val callback: LoadCallback<Unit> = mock()
        cxenseSdk.trackClick(WidgetItem("title", "url", null, mapOf()), callback)
        verify(callback).onError(any())
        verify(cxenseSdk, never()).trackClick(any<String>(), eq(callback))
    }

    @Test
    fun trackClickByUrl() {
        cxenseSdk.trackClick("url", mock())
        verify(call).enqueue(any<Callback<Any>>())
    }

    @Test
    fun loadWidgetRecommendations() {
        cxenseSdk.loadWidgetRecommendations("widgetId", callback = mock())
        verify(call).enqueue(any<Callback<Any>>())
        verify(cxenseSdk).defaultContentUser
    }

    @Test
    fun loadWidgetRecommendationsWithUser() {
        cxenseSdk.loadWidgetRecommendations("widgetId", user = mock(), callback = mock())
        verify(call).enqueue(any<Callback<Any>>())
        verify(cxenseSdk, never()).defaultContentUser
    }

    @Test
    fun reportWidgetVisibilities() {
        cxenseSdk.reportWidgetVisibilities(mock(), mock())
        verify(call).enqueue(any<Callback<Any>>())
    }

    @Test
    fun getUserSegmentIds() {
        cxenseSdk.getUserSegmentIds(listOf(mock()), listOf("123"), mock())
        verify(call).enqueue(any<Callback<Any>>())
    }

    @Test
    fun buildWithoutIdentities() {
        assertFailsWithMessage<IllegalArgumentException>("at least one user identity", "Expected fail for identities") {
            cxenseSdk.getUserSegmentIds(listOf(), listOf("sitegroupId"), mock())
        }
    }

    @Test
    fun buildWithoutSitegroups() {
        assertFailsWithMessage<IllegalArgumentException>(
            "at least one not empty site group id",
            "Expected fail for sitegroups"
        ) {
            cxenseSdk.getUserSegmentIds(listOf(mock()), listOf(""), mock())
        }
    }

    @Test
    fun getUserSegmentIdsNoConsent() {
        val callback: LoadCallback<List<String>> = mock()
        whenever(configuration.consentSettings).thenReturn(ConsentSettings().consentRequired(true))
        cxenseSdk.getUserSegmentIds(listOf(mock()), listOf("123"), callback)
        verify(callback).onError(any())
    }

    @Test
    fun getUser() {
        cxenseSdk.getUser(UserIdentity("cx", "id"), callback = mock())
        verify(call).enqueue(any<Callback<Any>>())
    }

    @Test
    fun getUserExternalData() {
        cxenseSdk.getUserExternalData("type", callback = mock())
        verify(call).enqueue(any<Callback<Any>>())
    }

    @Test
    fun setUserExternalData() {
        cxenseSdk.setUserExternalData(mock(), mock())
        verify(call).enqueue(any<Callback<Any>>())
    }

    @Test
    fun deleteUserExternalData() {
        cxenseSdk.deleteUserExternalData(mock(), mock())
        verify(call).enqueue(any<Callback<Any>>())
    }

    @Test
    fun getUserExternalLink() {
        cxenseSdk.getUserExternalLink("cxenseId", "type", mock())
        verify(call).enqueue(any<Callback<Any>>())
    }

    @Test
    fun addUserExternalLink() {
        cxenseSdk.addUserExternalLink("cxenseId", UserIdentity("cx", "id"), mock())
        verify(call).enqueue(any<Callback<Any>>())
    }

    @Test
    fun executePersistedQuery() {
        cxenseSdk.executePersistedQuery("url", "persistedQueryId", callback = mock<LoadCallback<Any>>())
        verify(call).enqueue(any<Callback<Any>>())
    }

    companion object {
        const val USER_ID = "user id"
        const val DEFAULT_USER_ID = "default user id"
    }
}
