package com.cxense.cxensesdk;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2018-09-17).
 */
class DeviceInfoProvider {
    private static final String TAG = DeviceInfoProvider.class.getSimpleName();
    private final Context context;

    DeviceInfoProvider(@NonNull Context context) {
        this.context = context;
    }

    DisplayMetrics getDisplayMetrics() {
        return context.getResources().getDisplayMetrics();
    }

    /**
     * Get version of the application in which SDK is used.
     *
     * @return application's version or 'null' if not found
     */
    @Nullable
    String getApplicationVersion() {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Problems during application version search", e);
        }
        return null;
    }

    /**
     * Get name of the application in which SDK is used.
     *
     * @return application's name or 'null' if not found
     */
    @Nullable
    String getApplicationName() {
        CharSequence label = context.getPackageManager().getApplicationLabel(context.getApplicationInfo());
        return label != null ? label.toString() : null;
    }

    /**
     * Gets current network status
     *
     * @return {@code NetworkStatus} instance
     */
    CxenseConfiguration.NetworkStatus getCurrentNetworkStatus() {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetworkInfo = manager.getActiveNetworkInfo();

        if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
            return CxenseConfiguration.NetworkStatus.NONE;
        }
        if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return CxenseConfiguration.NetworkStatus.WIFI;
        }
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        switch (telephonyManager.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_LTE:
                return CxenseConfiguration.NetworkStatus.MOBILE;
            default:
                return CxenseConfiguration.NetworkStatus.GPRS;
        }
    }
}
