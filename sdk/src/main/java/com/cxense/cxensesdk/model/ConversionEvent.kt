package com.cxense.cxensesdk.model

import com.cxense.cxensesdk.DependenciesProvider
import com.google.gson.annotations.SerializedName
import java.util.Collections

@Suppress("unused") // Public API.
class ConversionEvent private constructor(
    @SerializedName("userIds") val identities: List<UserIdentity>,
    @SerializedName("siteId") val siteId: String,
    @SerializedName("consent") val consentOptions: List<String>,
    @SerializedName("productId") val productId: String,
    @SerializedName("funnelStep") val funnelStep: String,
    @SerializedName("productPrice") val price: Double?,
    @SerializedName("productRenewalFrequency") val renewalFrequency: String?
) : Event(null) {
    @SerializedName("eventType")
    val eventType = EVENT_TYPE

    data class Builder(
        var siteId: String,
        var productId: String,
        var funnelStep: String,
        val identities: MutableList<UserIdentity> = mutableListOf(),
        var productPrice: Double? = null,
        var renewalFrequency: String? = null
    ) {

        fun addIdentities(vararg identities: UserIdentity) = apply { this.identities.addAll(identities) }
        fun addIdentities(identities: Iterable<UserIdentity>) = apply { this.identities.addAll(identities) }
        fun siteId(siteId: String) = apply { this.siteId = siteId }
        fun productId(productId: String) = apply { this.productId = productId }
        fun funnelStep(funnelStep: String) = apply { this.funnelStep = funnelStep }
        fun productPrice(productPrice: Double?) = apply { this.productPrice = productPrice }
        fun renewalFrequency(renewalFrequency: String?) = apply { this.renewalFrequency = renewalFrequency }

        fun build(): ConversionEvent {
            check(siteId.isNotEmpty()) {
                "Site id can't be empty"
            }
            check(productId.isNotEmpty()) {
                "Product id can't be empty"
            }
            check(productId.length <= MAX_LENGTH) {
                "Product id can't be longer than $MAX_LENGTH symbols"
            }
            check(funnelStep.isNotEmpty()) {
                "Funnel step can't be empty"
            }
            check(funnelStep.length <= MAX_LENGTH) {
                "Funnel step can't be longer than $MAX_LENGTH symbols"
            }
            renewalFrequency?.let {
                check(it.matches("^\\d{1,3}[dwMy][CR]$".toRegex())) {
                    "The renewal frequency has the format \"<number><units><type>\". The <number> is limited to 3 digits. Only 'd' (days), 'w' (weeks), 'M' (months) and 'y' (years) are supported as <units>. The <type> can be one of 'R' (relative to the time the user has converted) or 'C' (calendar-based: happening at the beginning of the <unit>)."
                }
            }

            return ConversionEvent(
                Collections.unmodifiableList(identities),
                siteId,
                DependenciesProvider.getInstance().cxenseConfiguration.consentOptionsValues,
                productId,
                funnelStep,
                productPrice,
                renewalFrequency
            )
        }
    }

    companion object {
        const val FUNNEL_TYPE_CONVERT_PRODUCT = "convertProduct"
        const val FUNNEL_TYPE_TERMINATE_PRODUCT = "terminateProduct"
        const val FUNNEL_TYPE_RENEW_PRODUCT = "renewProduct"
        internal const val EVENT_TYPE = "conversion"
        internal const val MAX_LENGTH = 30
    }
}