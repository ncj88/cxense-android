package io.piano.android.cxense.model

/**
 * Event status
 * @property eventId custom event id
 * @property isSent is event sent
 * @property exception exception thrown at sending
 */
@Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
class EventStatus(
    val eventId: String?,
    val isSent: Boolean,
    val exception: Exception? = null
)
