package com.cxense.cxensesdk.model

/**
 * User consent options
 */
@Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
enum class ConsentOption(val value: String) {
    /**
     * Explicit consent from user is required before processing data
     */
    CONSENT_REQUIRED("y"),
    /**
     * User allowed Page view tracking, DMP event tracking and browsing habit collection to understand a user’s interests and profile.
     */
    PV_ALLOWED("pv"),
    /**
     * User allowed personalisation of content recommendations and suggested content based on user interests and browsing habits.
     */
    RECS_ALLOWED("recs"),
    /**
     * User allowed audience segmentation - processing of browsing habits and first party data to include users in specific audience segments.
     */
    SEGMENT_ALLOWED("segment"),
    /**
     * User allowed targeting advertising based on browsing habits and audience segmentation.
     */
    AD_ALLOWED("ad")
}
