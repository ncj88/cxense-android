package io.piano.android.cxense

import io.piano.android.cxense.db.EventRecord
import io.piano.android.cxense.model.ConsentSettings
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import retrofit2.Call
import retrofit2.Response
import kotlin.test.Test
import kotlin.test.assertEquals

class SendTaskTest {
    private val call: Call<Any> = mock {
        on { execute() } doReturn Response.success(Any())
    }
    private val credentialsProvider: CredentialsProvider = mock {
        on { getDmpPushPersistentId() } doReturn "persisted"
        on { getUsername() } doReturn "user"
        on { getApiKey() } doReturn "key"
    }

    private val cxApi: CxApi = mock(defaultAnswer = { call })
    private val eventRepository: EventRepository = mock {
        on { deleteOutdatedEvents(any()) } doReturn 0
    }
    private val configuration: CxenseConfiguration = mock {
        on { consentSettings } doReturn ConsentSettings()
        on { credentialsProvider } doReturn credentialsProvider
        on { minimumNetworkStatus } doReturn CxenseConfiguration.NetworkStatus.NONE
    }
    private val deviceInfoProvider: DeviceInfoProvider = mock {
        on { getCurrentNetworkStatus() } doReturn CxenseConfiguration.NetworkStatus.WIFI
    }
    private val userProvider: UserProvider = mock()
    private val pageViewEventConverter: PageViewEventConverter = mock {
        on { extractQueryData(any(), any()) } doReturn mapOf()
    }
    private val performanceEventConverter: PerformanceEventConverter = mock {
        on { extractQueryData(any()) } doReturn (listOf("") to mapOf())
    }
    private val errorParser: ApiErrorParser = mock()

    private val sendTask = spy(
        SendTask(
            cxApi,
            eventRepository,
            configuration,
            deviceInfoProvider,
            userProvider,
            pageViewEventConverter,
            performanceEventConverter,
            errorParser
        ) {
        }
    )

    @Test
    fun sendEventsOneByOne() {
        val event1: EventRecord = mock()
        val event2: EventRecord = mock()
        var calls = 0
        val sendFunc: (EventRecord) -> Exception? = {
            calls++
            if (it == event1)
                null
            else BaseException()
        }
        sendTask.sendEventsOneByOne(listOf(event1, event2), sendFunc)
        assertEquals(2, calls)
    }

    @Test
    fun run() {
        whenever(credentialsProvider.getDmpPushPersistentId()).thenReturn("")
        doNothing().`when`(sendTask).sendPageViewEvents(any())
        doNothing().`when`(sendTask).sendDmpEventsViaApi(any())
        doNothing().`when`(sendTask).sendConversionEvents(any())
        sendTask.run()
        verify(eventRepository).deleteOutdatedEvents(any())
        verify(configuration).consentSettings
        verify(sendTask).sendPageViewEvents(any())
        verify(configuration).credentialsProvider
        verify(eventRepository).getNotSubmittedDmpEvents()
        verify(credentialsProvider).getDmpPushPersistentId()
        verify(credentialsProvider).getUsername()
        verify(credentialsProvider).getApiKey()
        verify(sendTask, never()).sendDmpEventsViaPersisted(any())
        verify(sendTask).sendDmpEventsViaApi(any())
        verify(eventRepository).getNotSubmittedConversionEvents()
        verify(sendTask).sendConversionEvents(any())
    }

    @Test
    fun runDmpViaPersisted() {
        doNothing().`when`(sendTask).sendPageViewEvents(any())
        doNothing().`when`(sendTask).sendDmpEventsViaPersisted(any())
        doNothing().`when`(sendTask).sendConversionEvents(any())
        sendTask.run()
        verify(eventRepository).deleteOutdatedEvents(any())
        verify(configuration).consentSettings
        verify(sendTask).sendPageViewEvents(any())
        verify(configuration).credentialsProvider
        verify(eventRepository).getNotSubmittedDmpEvents()
        verify(credentialsProvider).getDmpPushPersistentId()
        verify(credentialsProvider, never()).getUsername()
        verify(credentialsProvider, never()).getApiKey()
        verify(sendTask).sendDmpEventsViaPersisted(any())
        verify(sendTask, never()).sendDmpEventsViaApi(any())
        verify(eventRepository).getNotSubmittedConversionEvents()
        verify(sendTask).sendConversionEvents(any())
    }

    @Test
    fun runOffline() {
        whenever(deviceInfoProvider.getCurrentNetworkStatus()).thenReturn(CxenseConfiguration.NetworkStatus.NONE)
        whenever(configuration.minimumNetworkStatus).thenReturn(CxenseConfiguration.NetworkStatus.WIFI)
        sendTask.run()
        verify(eventRepository).deleteOutdatedEvents(any())
        verify(configuration, never()).consentSettings
        verify(sendTask, never()).sendPageViewEvents(any())
        verify(configuration, never()).credentialsProvider
        verify(eventRepository, never()).getNotSubmittedDmpEvents()
        verify(credentialsProvider, never()).getDmpPushPersistentId()
        verify(credentialsProvider, never()).getUsername()
        verify(credentialsProvider, never()).getApiKey()
        verify(sendTask, never()).sendDmpEventsViaPersisted(any())
        verify(sendTask, never()).sendDmpEventsViaApi(any())
        verify(eventRepository, never()).getNotSubmittedConversionEvents()
        verify(sendTask, never()).sendConversionEvents(any())
    }

    @Test
    fun runWithoutConsent() {
        whenever(configuration.consentSettings).thenReturn(ConsentSettings().consentRequired(true))
        sendTask.run()
        verify(eventRepository).deleteOutdatedEvents(any())
        verify(configuration).consentSettings
        verify(sendTask, never()).sendPageViewEvents(any())
        verify(configuration, never()).credentialsProvider
        verify(eventRepository, never()).getNotSubmittedDmpEvents()
        verify(credentialsProvider, never()).getDmpPushPersistentId()
        verify(credentialsProvider, never()).getUsername()
        verify(credentialsProvider, never()).getApiKey()
        verify(sendTask, never()).sendDmpEventsViaPersisted(any())
        verify(sendTask, never()).sendDmpEventsViaApi(any())
        verify(eventRepository, never()).getNotSubmittedConversionEvents()
        verify(sendTask, never()).sendConversionEvents(any())
    }
}
