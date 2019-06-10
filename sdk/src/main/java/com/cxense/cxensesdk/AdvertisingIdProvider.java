package com.cxense.cxensesdk;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2018-09-17).
 */
class AdvertisingIdProvider {
    private static final String TAG = AdvertisingIdProvider.class.getSimpleName();
    private static final long DELAY = 300;
    private final Context context;
    private final ScheduledExecutorService executor;
    private final Runnable getAdvertisingInfoTask;

    private AdvertisingIdClient.Info advertisingInfo;

    AdvertisingIdProvider(@NonNull Context context, @NonNull ScheduledExecutorService executor) {
        this.context = context;
        this.executor = executor;
        getAdvertisingInfoTask = () -> {
            try {
                advertisingInfo = AdvertisingIdClient.getAdvertisingIdInfo(this.context);
            } catch (IOException | GooglePlayServicesRepairableException e) {
                Log.e(TAG, e.getMessage(), e);
                initAdvertisingIdTask();
            } catch (GooglePlayServicesNotAvailableException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        };
        initAdvertisingIdTask();
    }

    private void initAdvertisingIdTask() {
        executor.schedule(getAdvertisingInfoTask, DELAY, TimeUnit.MILLISECONDS);
    }

    @Nullable
    String getDefaultUserId() {
        return advertisingInfo != null ? advertisingInfo.getId() : null;
    }

    boolean isLimitAdTrackingEnabled() {
        return advertisingInfo != null && advertisingInfo.isLimitAdTrackingEnabled();
    }
}
