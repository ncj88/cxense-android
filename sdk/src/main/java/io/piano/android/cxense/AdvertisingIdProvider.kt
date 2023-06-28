package io.piano.android.cxense

import android.content.Context
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.android.gms.common.GooglePlayServicesRepairableException
import timber.log.Timber
import java.io.IOException
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

/**
 * Provides AAID
 */
class AdvertisingIdProvider(
    private val context: Context,
    private val executor: ScheduledExecutorService,
) {
    private var advertisingInfo: AdvertisingIdClient.Info? = null

    private val getAdvertisingInfoTask: Runnable = Runnable {
        try {
            advertisingInfo = AdvertisingIdClient.getAdvertisingIdInfo(context)
        } catch (e: Exception) {
            Timber.e(e)
            if (e is IOException || e is GooglePlayServicesRepairableException) {
                initAdvertisingIdTask()
            }
        }
    }

    private fun initAdvertisingIdTask() = executor.schedule(getAdvertisingInfoTask, DELAY, TimeUnit.MILLISECONDS)

    val defaultUserId: String?
        get() = advertisingInfo?.id

    val limitAdTrackingEnabled: Boolean
        get() = advertisingInfo?.isLimitAdTrackingEnabled ?: false

    init {
        initAdvertisingIdTask()
    }

    companion object {
        private const val DELAY: Long = 300
    }
}
