package com.cxense.cxensesdk.model

/**
 * Base class for all events
 * @param eventId custom event id, that used for tracking locally.
 */
abstract class Event protected constructor(
    @Transient val eventId: String?
) {
    abstract val mergeKey: Int
}
