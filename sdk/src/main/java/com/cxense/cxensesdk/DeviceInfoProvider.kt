package com.cxense.cxensesdk

import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.telephony.TelephonyManager
import android.util.DisplayMetrics
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import timber.log.Timber

/**
 * Provides device info
 */
class DeviceInfoProvider(
    private val context: Context
) {
    val displayMetrics: DisplayMetrics by lazy { context.resources.displayMetrics }
    /**
     * Get version of the application in which SDK is used.
     *
     * @return application's version or 'null' if not found
     */
    val applicationVersion: String? by lazy {
        try {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            Timber.e(e, "Problems during application version search")
            null
        }
    }
    /**
     * Get name of the application in which SDK is used.
     *
     * @return application's name
     */
    val applicationName: String by lazy {
        context.packageManager.getApplicationLabel(context.applicationInfo).toString()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun NetworkCapabilities.toNetworkStatus(): CxenseConfiguration.NetworkStatus {
        return when {
            hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> CxenseConfiguration.NetworkStatus.WIFI
            hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> CxenseConfiguration.NetworkStatus.MOBILE
            hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) -> CxenseConfiguration.NetworkStatus.GPRS
            else -> CxenseConfiguration.NetworkStatus.NONE
        }
    }

    /**
     * Gets current network status
     *
     * @return {@code NetworkStatus} instance
     */
    fun getCurrentNetworkStatus(): CxenseConfiguration.NetworkStatus {
        val manager = ContextCompat.getSystemService(context, ConnectivityManager::class.java)
            ?: return CxenseConfiguration.NetworkStatus.NONE

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return manager.getNetworkCapabilities(manager.activeNetwork)?.toNetworkStatus()
                ?: CxenseConfiguration.NetworkStatus.NONE
        } else {
            @Suppress("DEPRECATION")
            val activeNetworkInfo = manager.activeNetworkInfo
            @Suppress("DEPRECATION")
            return when {
                activeNetworkInfo == null || !activeNetworkInfo.isConnected -> CxenseConfiguration.NetworkStatus.NONE
                activeNetworkInfo.type == ConnectivityManager.TYPE_WIFI -> CxenseConfiguration.NetworkStatus.WIFI
                else -> {
                    val telephonyManager =
                        ContextCompat.getSystemService(context, TelephonyManager::class.java)
                    when {
                        telephonyManager == null -> CxenseConfiguration.NetworkStatus.NONE
                        telephonyManager.networkType in MOBILE_NETWORK_TYPES -> CxenseConfiguration.NetworkStatus.MOBILE
                        else -> CxenseConfiguration.NetworkStatus.GPRS
                    }
                }
            }
        }
    }

    companion object {
        @JvmStatic
        val MOBILE_NETWORK_TYPES = arrayOf(
            TelephonyManager.NETWORK_TYPE_HSDPA,
            TelephonyManager.NETWORK_TYPE_HSPA,
            TelephonyManager.NETWORK_TYPE_HSPAP,
            TelephonyManager.NETWORK_TYPE_HSUPA,
            TelephonyManager.NETWORK_TYPE_LTE
        )
    }
}
