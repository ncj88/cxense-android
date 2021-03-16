package com.cxense.cxensesdk

import com.cxense.cxensesdk.db.EventRecord
import com.cxense.cxensesdk.model.ConsentSettings
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doNothing
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.mockito.stubbing.Answer
import retrofit2.Call
import retrofit2.Response
import kotlin.test.Test

class SendTaskTest {
    private val call: Call<Any> = mock {
        on { execute() } doReturn Response.success(Any())
    }
    private val credentialsProvider: CredentialsProvider = mock {
        on { getDmpPushPersistentId() } doReturn "persisted"
        on { getUsername() } doReturn "user"
        on { getApiKey() } doReturn "key"
    }

    private val cxApi: CxApi = mock<CxApi>(defaultAnswer = Answer { call })
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
        on { extractQueryData(any()) } doReturn (listOf("") to mapOf<String, String>())
    }
    private val errorParser: ApiErrorParser = mock()
    private val sendCallback: CxenseSdk.DispatchEventsCallback = mock()

    private val sendTask = spy(
        SendTask(
            cxApi,
            eventRepository,
            configuration,
            deviceInfoProvider,
            userProvider,
            pageViewEventConverter,
            performanceEventConverter,
            errorParser,
            sendCallback
        )
    )

    @Test
    fun sendDmpEventsViaApi() {
        val events = listOf<EventRecord>(mock(), mock())
        sendTask.sendDmpEventsViaApi(events)
        verify(cxApi).pushEvents(any())
        verify(eventRepository, times(events.size)).putEventRecordInDatabase(any())
        verify(errorParser).parseError(any())
        verify(sendCallback).onDispatch(any())
    }

    @Test
    fun sendEventsOneByOne() {
        val event1: EventRecord = mock()
        val event2: EventRecord = mock()
        val sendFunc: (EventRecord) -> Exception? = mock {
            on { invoke(eq(event1)) } doReturn null
            on { invoke(eq(event2)) } doThrow BaseException()
        }
        sendTask.sendEventsOneByOne(listOf(event1, event2), sendFunc)
        verify(sendFunc, times(2)).invoke(any())
        verify(sendCallback).onDispatch(any())
    }

    @Test
    fun sendDmpEventsViaPersisted() {
        val events = listOf<EventRecord>(mock(), mock())
        sendTask.sendDmpEventsViaPersisted(events)
        verify(sendTask).sendEventsOneByOne(eq(events), any())
        verify(credentialsProvider, times(events.size)).getDmpPushPersistentId()
        verify(cxApi, times(events.size)).trackDmpEvent(any(), any(), any())
        verify(eventRepository, times(events.size)).putEventRecordInDatabase(any())
        verify(errorParser, times(events.size)).parseError(any())
    }

    @Test
    fun sendPageViewEvents() {
        val events = listOf<EventRecord>(mock(), mock())
        sendTask.sendPageViewEvents(events)
        verify(sendTask).sendEventsOneByOne(eq(events), any())
        verify(cxApi, times(events.size)).trackInsightEvent(any())
        verify(eventRepository, times(events.size)).putEventRecordInDatabase(any())
        verify(errorParser, times(events.size)).parseError(any())
    }

    @Test
    fun sendConversionEvents() {
        val events = listOf<EventRecord>(mock(), mock())
        sendTask.sendConversionEvents(events)
        verify(sendTask).sendEventsOneByOne(eq(events), any())
        verify(cxApi, times(events.size)).pushConversionEvents(any())
        verify(eventRepository, times(events.size)).putEventRecordInDatabase(any())
        verify(errorParser, times(events.size)).parseError(any())
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
