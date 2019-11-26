package com.cxense.cxensesdk.model

import android.location.Location
import com.cxense.cxensesdk.DependenciesProvider
import okhttp3.HttpUrl
import java.util.Collections

class PageViewEvent(
    eventId: String?,
    val userId: String,
    val siteId: String,
    val location: String?,
    val contentId: String?,
    val referrer: String?,
    val accountId: Int?,
    val goalId: String?,
    val pageName: String?,
    val newUser: Boolean?,
    val userLocation: Location?,
    val customParameters: MutableList<CustomParameter>,
    val customUserParameters: MutableList<CustomParameter>,
    val externalUserIds: MutableList<ExternalUserId>
) : Event(eventId) {
    val eventType = EVENT_TYPE
    val time = System.currentTimeMillis()
    val rnd: String = "${time}${(Math.random() * 10E8).toInt()}"

    data class Builder(
        var siteId: String,
        var location: String? = null,
        var contentId: String? = null,
        var referrer: String? = null,
        var eventId: String? = null,
        var accountId: Int? = null,
        var goalId: String? = null,
        var pageName: String? = null,
        var newUser: Boolean? = null,
        var userLocation: Location? = null,
        var customParameters: MutableList<CustomParameter> = mutableListOf(),
        var customUserParameters: MutableList<CustomParameter> = mutableListOf(),
        var externalUserIds: MutableList<ExternalUserId> = mutableListOf()
    ) {
        private val userProvider = DependenciesProvider.getInstance().userProvider

        fun siteId(siteId: String) = apply { this.siteId = siteId }
        fun location(location: String?) = apply { this.location = location }
        fun contentId(contentId: String?) = apply { this.contentId = contentId }
        fun referrer(referrer: String?) = apply { this.referrer = referrer }
        fun eventId(eventId: String?) = apply { this.eventId = eventId }
        fun accountId(accountId: Int?) = apply { this.accountId = accountId }
        fun goalId(goalId: String?) = apply { this.goalId = goalId }
        fun pageName(pageName: String?) = apply { this.pageName = pageName }
        fun newUser(newUser: Boolean?) = apply { this.newUser = newUser }
        fun userLocation(userLocation: Location?) = apply { this.userLocation = userLocation }
        fun addCustomParameters(vararg customParameters: CustomParameter) =
            apply { this.customParameters.addAll(customParameters) }

        fun addCustomParameters(customParameters: Iterable<CustomParameter>) =
            apply { this.customParameters.addAll(customParameters) }

        fun addCustomUserParameters(vararg customUserParameters: CustomParameter) =
            apply { this.customUserParameters.addAll(customUserParameters) }

        fun addCustomUserParameters(customUserParameters: Iterable<CustomParameter>) =
            apply { this.customUserParameters.addAll(customUserParameters) }

        fun addExternalUserIds(vararg externalUserIds: ExternalUserId) =
            apply { this.externalUserIds.addAll(externalUserIds) }

        fun addExternalUserIds(externalUserIds: Iterable<ExternalUserId>) =
            apply { this.externalUserIds.addAll(externalUserIds) }

        fun build(): PageViewEvent {
            check(location != null || contentId != null) {
                "You should specify page location or content id"
            }
            check(siteId.isNotEmpty()) {
                "Site id can't be empty"
            }
            contentId?.let {
                check(it.isNotEmpty()) {
                    "Content id can't be empty"
                }
            }
            location?.let {
                check(HttpUrl.parse(it) != null) {
                    "You should provide valid url as location"
                }
            }
            referrer?.let {
                check(HttpUrl.parse(it) != null) {
                    "You should provide valid url as referrer"
                }
            }
            return PageViewEvent(
                eventId,
                userProvider.userId,
                siteId,
                location,
                contentId,
                referrer,
                accountId,
                goalId,
                pageName,
                newUser,
                userLocation,
                Collections.unmodifiableList(customParameters),
                Collections.unmodifiableList(customUserParameters),
                Collections.unmodifiableList(externalUserIds.takeLast(MAX_EXTERNAL_USER_IDS))
            )
        }
    }

    companion object {
        const val EVENT_TYPE = "pgv"
        const val MAX_EXTERNAL_USER_IDS = 5
    }
}