package com.cxense.cxensesdk.model

class QueueStatus(
    eventStatuses: List<EventStatus>
) {
    val sentEvents: List<EventStatus> = eventStatuses.filter { it.isSent }
    val notSentEvents: List<EventStatus> = eventStatuses.filterNot { it.isSent }
}
