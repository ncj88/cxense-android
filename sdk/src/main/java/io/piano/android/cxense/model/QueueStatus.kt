package io.piano.android.cxense.model

/**
 * Describes current events' sending queue status
 * @property sentEvents statuses for sent events
 * @property notSentEvents statuses for not sent events
 */
@Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
class QueueStatus(
    eventStatuses: List<EventStatus>
) {
    val sentEvents: List<EventStatus> = eventStatuses.filter { it.isSent }
    val notSentEvents: List<EventStatus> = eventStatuses.filterNot { it.isSent }
}
