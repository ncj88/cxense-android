package com.cxense.cxensesdk

import android.location.Location
import androidx.annotation.RestrictTo
import com.cxense.cxensesdk.db.EventRecord
import com.cxense.cxensesdk.model.Event
import com.cxense.cxensesdk.model.PageViewEvent
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Supports [PageViewEvent] to [EventRecord] converting
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
class PageViewEventConverter(
    private val gson: Gson,
    private val configuration: CxenseConfiguration,
    private val deviceInfoProvider: DeviceInfoProvider
) : EventConverter() {
    override fun canConvert(event: Event): Boolean = event is PageViewEvent

    private fun PageViewEvent.toQueryMap(): Map<String, String?> {
        val offset = with(Calendar.getInstance()) {
            TimeUnit.MILLISECONDS.toMinutes(timeZone.getOffset(timeInMillis).toLong())
        }
        val (resolution, density) = with(deviceInfoProvider.displayMetrics) {
            "${widthPixels}x$heightPixels" to density.toString()
        }
        val pairs = sequenceOf(
            SITE_ID to siteId,
            VERSION to DEFAULT_API_VERSION,
            TYPE to eventType,
            ACCOUNT to accountId?.toString(),
            LOCATION to (contentId?.let { "http://$siteId.content.id/$contentId" } ?: location),
            REFERRER to referrer,
            PAGE_NAME to pageName,
            TIME to time.toString(),
            // The client's timezone.
            TIME_OFFSET to offset.toString(),
            RESOLUTION to resolution,
            START_RESOLUTION to resolution,
            // Device color depth.
            COLOR to DEFAULT_COLOR_DEPTH, // Android uses ARGB_8888 32bit from version 2.3 (API 10)
            DENSITY to density,
            RND to rnd,
            // Is Java enabled
            JAVA to "0", // No, we don't have Java 😁
            LANGUAGE to with(Locale.getDefault()) { "${language}_$country" },
            CKP to userId,
            ENCODING to DEFAULT_ENCODING,
            FLASH to "0",
            NEW_USER to newUser?.let { if (it) "1" else "0" },
            CONSENT to configuration.consentOptionsValues.takeUnless { it.isEmpty() }?.joinToString()
        )
        val appMetadata = if (configuration.autoMetaInfoTrackingEnabled) sequenceOf(
            "${CUSTOM_PARAMETER_PREFIX}app" to deviceInfoProvider.applicationName,
            "${CUSTOM_PARAMETER_PREFIX}appv" to (deviceInfoProvider.applicationVersion ?: "")
        ) else emptySequence()
        val userLocation = userLocation?.toPairs() ?: emptySequence()
        val ids = externalUserIds.asSequence().withIndex().flatMap { (i, id) ->
            sequenceOf(
                "$EXTERNAL_USER_KEY$i" to id.userType,
                "$EXTERNAL_USER_VALUE$i" to id.userId
            )
        }
        val result = pairs + appMetadata + userLocation + ids +
                customParameters.asSequence().map { "$CUSTOM_PARAMETER_PREFIX${it.name}" to it.value } +
                customUserParameters.asSequence().map { "$CUSTOM_USER_PARAMETER_PREFIX${it.name}" to it.value }
        return result.toMap()
    }

    private fun Location.toPairs(): Sequence<Pair<String, String?>> {
        return sequenceOf(
            LATITUDE to latitude.toString(),
            LONGITUDE to longitude.toString(),
            ACCURACY to accuracy.takeIf { hasAccuracy() }?.toString(),
            ALTITUDE to altitude.takeIf { hasAltitude() }?.toString(),
            HEADING to bearing.takeIf { hasBearing() }?.toString(),
            SPEED to speed.takeIf { hasSpeed() }?.toString()
        )
    }

    internal fun extractQueryData(eventRecord: EventRecord, fixUserIdFunc: () -> String): Map<String, String> {
        return gson.fromJson<MutableMap<String, String>>(
            eventRecord.data,
            object : TypeToken<MutableMap<String, String>>() {}.type
        ).apply {
            if (this[CKP].isNullOrEmpty()) {
                this[CKP] = fixUserIdFunc()
            }
        }
    }

    override fun toEventRecord(event: Event): EventRecord? =
        (event as? PageViewEvent)?.run {
            EventRecord(
                PageViewEvent.EVENT_TYPE,
                eventId,
                gson.toJson(toQueryMap()),
                userId,
                rnd,
                time
            )
        }

    internal fun updateActiveTimeData(data: String, activeTime: Long): String =
        // some black magic with map
        with(
            gson.fromJson<MutableMap<String, String?>>(
                data,
                object : TypeToken<MutableMap<String, String?>>() {}.type
            )
        ) {
            this[ACTIVE_RND] = this[RND]
            this[ACTIVE_TIME] = this[TIME]
            this[ACTIVE_SPENT_TIME] = activeTime.toString()
            gson.toJson(this)
        }

    companion object {
        const val TIME = "ltm"
        const val RND = "rnd"
        const val ACTIVE_RND = "arnd"
        const val ACTIVE_TIME = "altm"
        const val ACTIVE_SPENT_TIME = "aatm"
        internal const val CKP = "ckp"
        internal const val EXTERNAL_USER_KEY = "eit"
        internal const val EXTERNAL_USER_VALUE = "eid"
        internal const val CONSENT = "con"
        internal const val CUSTOM_PARAMETER_PREFIX = "cp_"
        private const val CUSTOM_USER_PARAMETER_PREFIX = "cp_u_"
        private const val DEFAULT_API_VERSION = "1"
        private const val DEFAULT_COLOR_DEPTH = "32"
        private const val DEFAULT_ENCODING = "UTF-8"
        // Map keys constants
        internal const val VERSION = "ver"
        internal const val TYPE = "typ"
        private const val ACCOUNT = "acc"
        internal const val SITE_ID = "sid"
        internal const val LOCATION = "loc"
        internal const val REFERRER = "ref"
        private const val PAGE_NAME = "pgn"
        internal const val TIME_OFFSET = "tzo"
        private const val RESOLUTION = "res"
        private const val START_RESOLUTION = "wsz"
        private const val COLOR = "col"
        private const val DENSITY = "dpr"
        private const val JAVA = "jav"
        private const val LANGUAGE = "bln"
        internal const val ENCODING = "chs"
        private const val FLASH = "fls"
        private const val NEW_USER = "new"
        private const val LATITUDE = "plat"
        private const val LONGITUDE = "plon"
        private const val ACCURACY = "pacc"
        private const val ALTITUDE = "palt"
        private const val HEADING = "phed"
        private const val SPEED = "pspd"
    }
}
