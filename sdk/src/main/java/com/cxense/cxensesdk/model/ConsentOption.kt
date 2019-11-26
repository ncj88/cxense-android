package com.cxense.cxensesdk.model

@Suppress("unused") // Public API.
enum class ConsentOption(val value: String) {
    CONSENT_REQUIRED("y"),
    PV_ALLOWED("pv"),
    RECS_ALLOWED("recs"),
    SEGMENT_ALLOWED("segment"),
    AD_ALLOWED("ad")
}