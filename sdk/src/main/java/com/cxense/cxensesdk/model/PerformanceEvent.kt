package com.cxense.cxensesdk.model

import com.cxense.cxensesdk.DependenciesProvider
import com.google.gson.annotations.SerializedName
import java.util.Collections
import java.util.Date
import java.util.concurrent.TimeUnit

@Suppress("unused") // Public API.
class PerformanceEvent(
    eventId: String?,
    @SerializedName(USER_IDS) val identities: List<UserIdentity>,
    @SerializedName(SITE_ID) val siteId: String,
    @SerializedName(ORIGIN) val origin: String,
    @SerializedName(TYPE) val eventType: String,
    @SerializedName(PRND) val prnd: String?,
    @SerializedName(TIME) val time: Long,
    @SerializedName(SEGMENT_IDS) val segments: List<String>?,
    @SerializedName(CUSTOM_PARAMETERS) val customParameters: List<CustomParameter>,
    @SerializedName("consent") val consentOptions: List<String>
) : Event(eventId) {

    @SerializedName(RND)
    val rnd: String = "${System.currentTimeMillis()}${(Math.random() * 10E8).toInt()}"

    data class Builder(
        var siteId: String,
        var origin: String,
        var eventType: String,
        var identities: MutableList<UserIdentity> = mutableListOf(),
        var eventId: String? = null,
        var prnd: String? = null,
        var time: Long = System.currentTimeMillis(),
        var segments: MutableList<String> = mutableListOf(),
        var customParameters: MutableList<CustomParameter> = mutableListOf()
    ) {
        fun addIdentities(vararg identities: UserIdentity) = apply { this.identities.addAll(identities) }
        fun addIdentities(identities: Iterable<UserIdentity>) = apply { this.identities.addAll(identities) }
        fun siteId(siteId: String) = apply { this.siteId = siteId }
        fun origin(origin: String) = apply { this.origin = origin }
        fun eventType(type: String) = apply { this.eventType = type }
        fun eventId(eventId: String?) = apply { this.eventId = eventId }
        fun prnd(prnd: String?) = apply { this.prnd = prnd }
        fun currentTime() = apply { this.time = System.currentTimeMillis() }
        fun time(date: Date) = apply { this.time = TimeUnit.MILLISECONDS.toSeconds(date.time) }
        fun addSegments(vararg segments: String) = apply { this.segments.addAll(segments) }
        fun addSegments(segments: Iterable<String>) = apply { this.segments.addAll(segments) }
        fun addCustomParameters(vararg customParameters: CustomParameter) =
            apply { this.customParameters.addAll(customParameters) }

        fun addCustomParameters(customParameters: Iterable<CustomParameter>) =
            apply { this.customParameters.addAll(customParameters) }

        fun build(): PerformanceEvent {
            check(identities.isNotEmpty()) {
                "You should supply at least one user identity"
            }
            check(siteId.isNotEmpty()) {
                "Site id can't be empty"
            }
            check(eventType.isNotEmpty()) {
                "Event type can't be empty"
            }
            check(origin.matches(ORIGIN_REGEX.toRegex())) {
                "Origin must be prefixed by the customer prefix."
            }

            return PerformanceEvent(
                eventId,
                Collections.unmodifiableList(identities),
                siteId,
                origin,
                eventType,
                prnd,
                time,
                segments.takeUnless { it.isEmpty() }?.let { Collections.unmodifiableList(it) },
                Collections.unmodifiableList(customParameters),
                DependenciesProvider.getInstance().cxenseConfiguration.consentOptionsValues
            )
        }
    }

    companion object {
        const val ORIGIN_REGEX = "\\w{3}-[\\w-]+"
        const val TIME = "time"
        const val USER_IDS = "userIds"
        const val PRND = "prnd"
        const val RND = "rnd"
        const val SITE_ID = "siteId"
        const val ORIGIN = "origin"
        const val TYPE = "type"
        const val SEGMENT_IDS = "segmentIds"
        const val CUSTOM_PARAMETERS = "customParameters"
    }
}
