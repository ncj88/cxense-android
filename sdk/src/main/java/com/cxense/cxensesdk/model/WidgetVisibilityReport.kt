package com.cxense.cxensesdk.model

import com.google.gson.annotations.SerializedName

/**
 * Request object for reporting widgets visibilities to server.
 *
 */
class WidgetVisibilityReport(
    @SerializedName("impressions") val impressions: List<Impression>
)
