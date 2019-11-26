package com.cxense.cxensesdk

import java.util.concurrent.TimeUnit

val DEFAULT_DISPATCH_PERIOD = TimeUnit.SECONDS.toMillis(300)
/**
 * Minimum dispatch period for events in seconds
 */
const val MIN_DISPATCH_PERIOD_SECONDS = 10L
/**
 * Minimum dispatch period for events in milliseconds
 */
val MIN_DISPATCH_PERIOD = TimeUnit.SECONDS.toMillis(MIN_DISPATCH_PERIOD_SECONDS)
/**
 * Default outdate period for events in milliseconds
 */
val DEFAULT_OUTDATED_PERIOD = TimeUnit.DAYS.toMillis(7)
/**
 * Minimum outdate period for events in milliseconds
 */
val MIN_OUTDATE_PERIOD_SECONDS = TimeUnit.MINUTES.toSeconds(10)
/**
 * Minimum outdate period for events in milliseconds
 */
val MIN_OUTDATE_PERIOD = TimeUnit.MINUTES.toMillis(10)

/**
 * Endpoint for getting user segments
 */
const val ENDPOINT_USER_SEGMENTS = "profile/user/segment"
/**
 * Endpoint for getting user profile
 */
const val ENDPOINT_USER_PROFILE = "profile/user"
/**
 * Endpoint for getting user external data
 */
const val ENDPOINT_READ_USER_EXTERNAL_DATA = "profile/user/external/read"
/**
 * Endpoint for updating user external data
 */
const val ENDPOINT_UPDATE_USER_EXTERNAL_DATA = "profile/user/external/update"
/**
 * Endpoint for deleting user external data
 */
const val ENDPOINT_DELETE_USER_EXTERNAL_DATA = "profile/user/external/delete"
/**
 * Endpoint for getting user's external identity mapping
 */
const val ENDPOINT_READ_USER_EXTERNAL_LINK = "profile/user/external/link"
/**
 * Endpoint for creating new user's external identity mapping
 */
const val ENDPOINT_UPDATE_USER_EXTERNAL_LINK = "profile/user/external/link/update"
/**
 * Endpoint for pushing DMP events
 */
const val ENDPOINT_PUSH_DMP_EVENTS = "dmp/push"
