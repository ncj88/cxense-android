package io.piano.android.cxense

import io.piano.android.cxense.db.EventRecord
import io.piano.android.cxense.model.EventDataRequest
import io.piano.android.cxense.model.EventStatus
import timber.log.Timber

/**
 * Runnable, which sends all events from local database to server
 *
 */
class SendTask(
    private val cxApi: CxApi,
    private val eventRepository: EventRepository,
    private val configuration: CxenseConfiguration,
    private val deviceInfoProvider: DeviceInfoProvider,
    private val userProvider: UserProvider,
    private val pageViewEventConverter: PageViewEventConverter,
    private val performanceEventConverter: PerformanceEventConverter,
    private val errorParser: ApiErrorParser,
    var sendCallback: CxenseSdk.DispatchEventsCallback?
) : Runnable {

    private fun EventRecord.toEventStatus(e: Exception? = null) =
        EventStatus(
            customId,
            isSent,
            e
        )

    private fun List<EventRecord>.notifyCallback(e: Exception? = null) = map { it.toEventStatus(e) }.notifyCallback()

    private fun List<EventStatus>.notifyCallback() = sendCallback?.onDispatch(this)

    internal fun sendDmpEventsViaApi(events: List<EventRecord>) {
        try {
            events.map { it.data }
                .takeUnless { it.isEmpty() }
                ?.let { data ->
                    events.notifyCallback(
                        with(cxApi.pushEvents(EventDataRequest(data)).execute()) {
                            if (isSuccessful) {
                                events.forEach { r ->
                                    r.isSent = true
                                    eventRepository.putEventRecordInDatabase(r)
                                }
                            }
                            errorParser.parseError(this)
                        }
                    )
                }
        } catch (e: Exception) {
            events.notifyCallback(e)
        }
    }

    internal fun sendEventsOneByOne(events: List<EventRecord>, sendFunc: (EventRecord) -> Exception?) {
        events.map { record ->
            try {
                record.toEventStatus(sendFunc(record))
            } catch (e: Exception) {
                record.toEventStatus(e)
            }
        }.notifyCallback()
    }

    internal fun sendDmpEventsViaPersisted(events: List<EventRecord>) = sendEventsOneByOne(events) { record ->
        performanceEventConverter.extractQueryData(record)?.let { (segments, data) ->
            with(
                cxApi.trackDmpEvent(
                    configuration.credentialsProvider.getDmpPushPersistentId(),
                    segments ?: listOf(),
                    data
                ).execute()
            ) {
                if (isSuccessful) {
                    record.isSent = true
                    eventRepository.putEventRecordInDatabase(record)
                }
                errorParser.parseError(this)
            }
        }
    }

    internal fun sendPageViewEvents(events: List<EventRecord>) = sendEventsOneByOne(events) { record ->
        with(cxApi.trackInsightEvent(pageViewEventConverter.extractQueryData(record, userProvider::userId)).execute()) {
            if (isSuccessful) {
                record.isSent = true
                eventRepository.putEventRecordInDatabase(record)
            }
            errorParser.parseError(this)
        }
    }

    internal fun sendConversionEvents(events: List<EventRecord>) = sendEventsOneByOne(events) { record ->
        with(cxApi.pushConversionEvents(EventDataRequest(listOf(record.data))).execute()) {
            if (isSuccessful) {
                record.isSent = true
                eventRepository.putEventRecordInDatabase(record)
            }
            errorParser.parseError(this)
        }
    }

    override fun run() {
        try {
            eventRepository.deleteOutdatedEvents(configuration.outdatePeriod)
            if (deviceInfoProvider.getCurrentNetworkStatus() < configuration.minimumNetworkStatus) {
                return
            }
            val pvDenied = with(configuration.consentSettings) {
                consentRequired && !pvAllowed
            }
            if (pvDenied) {
                return
            }
            sendPageViewEvents(eventRepository.getNotSubmittedPvEvents())
            with(configuration.credentialsProvider) {
                eventRepository.getNotSubmittedDmpEvents().let { events ->
                    if (getDmpPushPersistentId().isNotEmpty()) {
                        sendDmpEventsViaPersisted(events)
                    } else if (getUsername().isNotEmpty() && getApiKey().isNotEmpty()) {
                        sendDmpEventsViaApi(events)
                    }
                }
            }
            sendConversionEvents(eventRepository.getNotSubmittedConversionEvents())
        } catch (e: Exception) {
            Timber.e(e, "Error at sending data")
        }
    }
}
