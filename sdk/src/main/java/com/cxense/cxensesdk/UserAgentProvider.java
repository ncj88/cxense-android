package com.cxense.cxensesdk;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.NonNull;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class UserAgentProvider {
    private static final String TAG = UserAgentProvider.class.getSimpleName();
    private static final long DELAY = 300;
    private String userAgent;

    public UserAgentProvider(String sdkVersion, Context context, ScheduledExecutorService executor) {
        executor.schedule(() -> {
            userAgent = String.format("cx-sdk/%s %s", sdkVersion, getDefaultUserAgent(context));
        }, DELAY, TimeUnit.MILLISECONDS);
    }

    /**
     * Returns the user-agent used by the SDK
     */
    @NonNull
    String getUserAgent() {
        return userAgent;
    }

    /**
     * Gets default user-agent from Android
     *
     * @return system default user-agent
     */
    private String getDefaultUserAgent(Context context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                return WebSettings.getDefaultUserAgent(context);
            }
            return new WebView(context).getSettings().getUserAgentString();
        } catch (Exception e) {
            /*
            This block is needed as attempt to avoid problem with Android System WebView
            service's update during which any requests to WebViews will be finished
            with android.content.pm.PackageManager$NameNotFoundException.

            What is important here, that 'user-agent' is required param in Cxense Insight API,
            so, we need to provide it. We can use 'http.agent' property's value here, but
            it provides less details about device than WebView. That is why property's value
            is used without defaultUserAgent field's initialization.

            Best practise here - always using WebView's 'user-agent' string.

            Bug in Android issue tracker can be found here:
            https://code.google.com/p/android/issues/detail?id=175124

            Good explanation of the problem can be found here:
            https://bugs.chromium.org/p/chromium/issues/detail?id=506369
             */
            Log.e(TAG, e.getMessage(), e);
        }
        return System.getProperty("http.agent", "");
    }
}
