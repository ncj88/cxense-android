package com.cxense.cxensesdk.model

import android.location.Location
import com.cxense.cxensesdk.DependenciesProvider
import com.cxense.cxensesdk.UserProvider
import okhttp3.HttpUrl
import java.util.Collections

/**
 * Tracking page view event description.
 * Page view event has support for two modes: standard page view event and URL-less mode for content view event
 * @property eventType Predefined event type
 * @property time Event timestamp
 * @property rnd Event rnd, uniquely identifies a page-view request.
 * @property siteId The Cxense site identifier.
 * @property location The URL of the page.
 * @property contentId The content id for URL-less mode.
 * @property referrer The URL of the referring page.
 * @property eventId Custom event id, that used for tracking locally.
 * @property accountId The Cxense account identifier.
 * @property pageName The page name.
 * @property newUser Hint to indicate if this looks like a new user.
 * @property userLocation User geo location
 * @property customParameters Custom parameters.
 * @property customUserParameters Custom user profile parameters.
 * @property externalUserIds External user ids.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
class PageViewEvent private constructor(
    eventId: String?,
    val userId: String,
    val siteId: String,
    val location: String?,
    val contentId: String?,
    val referrer: String?,
    val accountId: Int?,
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

    /**
     * @property siteId the Cxense site identifier.
     * @property location Sets the URL of the page. Must be a syntactically valid URL, or else the event will be dropped.
     * This value will be ignored, if you setup content id.
     * @property contentId Sets content id for URL-less mode. Forces to ignore page location value.
     * @property referrer Sets the URL of the referring page. Must be a syntactically valid URL
     * @property eventId custom event id, that used for tracking locally.
     * @property accountId the Cxense account identifier.
     * @property pageName the page name.
     * @property newUser hint to indicate if this looks like a new user.
     * @property userLocation user geo location
     * @property customParameters custom parameters.
     * @property customUserParameters custom user profile parameters.
     * @property externalUserIds external user ids.
     */
    data class Builder internal constructor(
        val userProvider: UserProvider,
        var siteId: String,
        var location: String? = null,
        var contentId: String? = null,
        var referrer: String? = null,
        var eventId: String? = null,
        var accountId: Int? = null,
        var pageName: String? = null,
        var newUser: Boolean? = null,
        var userLocation: Location? = null,
        var customParameters: MutableList<CustomParameter> = mutableListOf(),
        var customUserParameters: MutableList<CustomParameter> = mutableListOf(),
        var externalUserIds: MutableList<ExternalUserId> = mutableListOf()
    ) {
        /**
         * Initialize Builder with required parameters
         */
        @JvmOverloads
        constructor(
            siteId: String,
            location: String? = null,
            contentId: String? = null,
            referrer: String? = null,
            eventId: String? = null,
            accountId: Int? = null,
            pageName: String? = null,
            newUser: Boolean? = null,
            userLocation: Location? = null,
            customParameters: MutableList<CustomParameter> = mutableListOf(),
            customUserParameters: MutableList<CustomParameter> = mutableListOf(),
            externalUserIds: MutableList<ExternalUserId> = mutableListOf()
        ) : this(
            DependenciesProvider.getInstance().userProvider,
            siteId,
            location,
            contentId,
            referrer,
            eventId,
            accountId,
            pageName,
            newUser,
            userLocation,
            customParameters,
            customUserParameters,
            externalUserIds
        )

        /**
         * Sets site identifier.
         * @param siteId the Cxense site identifier.
         */
        fun siteId(siteId: String) = apply { this.siteId = siteId }

        /**
         * Sets location URL
         * @param location Sets the URL of the page. Must be a syntactically valid URL, or else the event will be dropped.
         */
        fun location(location: String?) = apply { this.location = location }

        /**
         * Sets content id
         * @param contentId Sets content id for URL-less mode. Forces to ignore page location value.
         */
        fun contentId(contentId: String?) = apply { this.contentId = contentId }

        /**
         * Sets referrer URL
         * @param referrer Sets the URL of the referring page. Must be a syntactically valid URL
         */
        fun referrer(referrer: String?) = apply { this.referrer = referrer }

        /**
         * Sets custom event id
         * @param eventId custom event id, that used for tracking locally.
         */
        fun eventId(eventId: String?) = apply { this.eventId = eventId }

        /**
         * Sets account identifier.
         * @param accountId the Cxense account identifier.
         */
        fun accountId(accountId: Int?) = apply { this.accountId = accountId }

        /**
         * Sets the page name.
         * @param pageName the page name.
         */
        fun pageName(pageName: String?) = apply { this.pageName = pageName }

        /**
         * Sets new user flag.
         * @param newUser hint to indicate if this looks like a new user.
         */
        fun newUser(newUser: Boolean?) = apply { this.newUser = newUser }

        /**
         * Sets user geo location.
         * @param userLocation user geo location
         */
        fun userLocation(userLocation: Location?) = apply { this.userLocation = userLocation }

        /**
         * Add custom parameters.
         * @param customParameters one or many [CustomParameter] objects
         */
        fun addCustomParameters(vararg customParameters: CustomParameter) =
            apply { this.customParameters.addAll(customParameters) }

        /**
         * Add custom parameters.
         * @param customParameters [Iterable] with [CustomParameter] objects
         */
        fun addCustomParameters(customParameters: Iterable<CustomParameter>) =
            apply { this.customParameters.addAll(customParameters) }

        /**
         * Add custom user profile parameters.
         * @param customUserParameters one or many [CustomParameter] objects
         */
        fun addCustomUserParameters(vararg customUserParameters: CustomParameter) =
            apply { this.customUserParameters.addAll(customUserParameters) }

        /**
         * Add custom user profile parameters.
         * @param customUserParameters [Iterable] with [CustomParameter] objects
         */
        fun addCustomUserParameters(customUserParameters: Iterable<CustomParameter>) =
            apply { this.customUserParameters.addAll(customUserParameters) }

        /**
         * Adds external user ids for this event.
         * You can add a maximum of [MAX_EXTERNAL_USER_IDS] external user ids, if you add more, then last will be used.
         * @param externalUserIds one or many [ExternalUserId] objects
         */
        fun addExternalUserIds(vararg externalUserIds: ExternalUserId) =
            apply { this.externalUserIds.addAll(externalUserIds) }

        /**
         * Adds external user ids for this event.
         * You can add a maximum of [MAX_EXTERNAL_USER_IDS] external user ids, if you add more, then last will be used.
         * @param externalUserIds [Iterable] with [ExternalUserId] objects
         */
        fun addExternalUserIds(externalUserIds: Iterable<ExternalUserId>) =
            apply { this.externalUserIds.addAll(externalUserIds) }

        /**
         * Builds page view event
         * @throws [IllegalArgumentException] if constraints failed
         */
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
