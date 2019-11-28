package com.cxense.cxensesdk.model

@Suppress("unused") // Public API.
class QueueStatus(
    eventStatuses: List<EventStatus>
) {
    val sentEvents: List<EventStatus> = eventStatuses.filter { it.isSent }
    val notSentEvents: List<EventStatus> = eventStatuses.filterNot { it.isSent }
}
