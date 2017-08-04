package com.cxense.cxensesdk;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;
import android.webkit.URLUtil;

import com.cxense.Preconditions;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Cxense SDK configuration class
 *
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-07-03).
 */

public final class CxenseConfiguration {
    /**
     * Default dispatch period for events in milliseconds
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public static final long DEFAULT_DISPATCH_PERIOD = TimeUnit.SECONDS.toMillis(300);
    /**
     * Minimum dispatch period for events in milliseconds
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public static final long MIN_DISPATCH_PERIOD = TimeUnit.SECONDS.toMillis(10);
    /**
     * Default outdate period for events in milliseconds
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public static final long DEFAULT_OUTDATED_PERIOD = TimeUnit.DAYS.toMillis(7);
    /**
     * Minimum outdate period for events in milliseconds
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public static final long MIN_OUTDATE_PERIOD = TimeUnit.MINUTES.toMillis(10);
    /**
     * Default "base url" for url-less mode
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public static final String DEFAULT_URL_LESS_BASE_URL = "http://example.com/";

    static final long DISPATCH_INITIAL_DELAY = TimeUnit.SECONDS.toMillis(30);
    private String username;
    private String apiKey;
    private boolean isAutoMetaInfoTrackingEnabled = true;
    private long dispatchPeriod = DEFAULT_DISPATCH_PERIOD;
    private NetworkRestriction networkRestriction = NetworkRestriction.NONE;
    private DispatchMode dispatchMode = DispatchMode.ONLINE;
    private long outdatePeriod = DEFAULT_OUTDATED_PERIOD;
    private String urlLessBaseUrl = DEFAULT_URL_LESS_BASE_URL;

    CxenseConfiguration() {
    }

    /**
     * Gets username
     *
     * @return username
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public String getUsername() {
        return username;
    }

    /**
     * Sets username
     *
     * @param username username
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public void setUsername(@NonNull String username) {
        Preconditions.checkStringForNullOrEmpty(username, "username");
        this.username = username;
        CxenseSdk.getInstance().updateAuth(this.username, this.apiKey);
    }

    /**
     * Gets Api key
     *
     * @return api key
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public String getApiKey() {
        return apiKey;
    }

    /**
     * Sets api key
     *
     * @param apiKey api key
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public void setApiKey(@NonNull String apiKey) {
        Preconditions.checkStringForNullOrEmpty(apiKey, "apiKey");
        this.apiKey = apiKey;
        CxenseSdk.getInstance().updateAuth(this.username, this.apiKey);
    }

    /**
     * Shows if meta information about application should be tracked automatically.
     * Note: if 'true', events will be enriched by custom parameters with information
     * about application's name, version and etc.
     *
     * @return 'true' if meta information should be tracked
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public boolean isAutoMetaInfoTrackingEnabled() {
        return isAutoMetaInfoTrackingEnabled;
    }

    /**
     * Enable or disable automatic tracking of application's meta information.
     *
     * @param isEnabled if 'true' meta information will be tracked automatically
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public void setAutoMetaInfoTrackingEnabled(boolean isEnabled) {
        isAutoMetaInfoTrackingEnabled = isEnabled;
    }

    /**
     * Returns the dispatch mode
     *
     * @return the dispatch mode
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public DispatchMode getDispatchMode() {
        return dispatchMode;
    }

    /**
     * Sets the dispatch mode. The default value is DispatchMode.ONLINE
     *
     * @param mode the new dispatch mode
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public void setDispatchMode(DispatchMode mode) {
        dispatchMode = mode;
    }

    /**
     * Returns the network restriction
     *
     * @return the network restriction
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public NetworkRestriction getNetworkRestriction() {
        return networkRestriction;
    }

    /**
     * Set the network restriction which should apply for automatic dispatching
     * of events. The default value is NetworkRestriction.NONE
     *
     * @param restriction the restriction which should be enforced
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public void setNetworkRestriction(NetworkRestriction restriction) {
        networkRestriction = restriction;
    }

    /**
     * Gets current dispatch period in milliseconds
     *
     * @return current dispatch period
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public long getDispatchPeriod() {
        return dispatchPeriod;
    }

    /**
     * Sets dispatch period for the dispatcher. The dispatcher will check for events to dispatch
     * every {@code period}.
     *
     * @param period the dispatch period
     * @param unit   the time unit of the period parameter
     * @throws IllegalArgumentException if period smaller then {@link #MIN_DISPATCH_PERIOD}
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public void setDispatchPeriod(long period, TimeUnit unit) {
        long millis = unit.toMillis(period);
        if (millis < MIN_DISPATCH_PERIOD)
            throw new IllegalArgumentException(String.format(Locale.US, "period must be greater than %d seconds", TimeUnit.MILLISECONDS.toSeconds(MIN_DISPATCH_PERIOD)));
        dispatchPeriod = millis;
        CxenseSdk.getInstance().initSendTaskSchedule();
    }

    /**
     * Gets current outdate period in milliseconds.
     *
     * @return current outdate period
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public long getOutdatePeriod() {
        return outdatePeriod;
    }

    /**
     * Sets outdate period for events. The dispatcher will delete all events, that tracked more than {@code period}.
     *
     * @param period the dispatch period
     * @param unit   the time unit of the period parameter
     * @throws IllegalArgumentException if period smaller than {@link #MIN_OUTDATE_PERIOD}
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public void setOutdatedPeriod(long period, TimeUnit unit) {
        long millis = unit.toMillis(period);
        if (millis < MIN_OUTDATE_PERIOD)
            throw new IllegalArgumentException(String.format(Locale.US, "period must be greater than %d seconds", TimeUnit.MILLISECONDS.toSeconds(MIN_OUTDATE_PERIOD)));
        this.outdatePeriod = millis;
    }

    /**
     * Gets current base url for url-less mode.
     *
     * @return current base url
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public String getUrlLessBaseUrl() {
        return urlLessBaseUrl;
    }

    /**
     * Sets base url for url-less mode.
     *
     * @param baseUrl base url. Must be a syntactically valid http/https URL
     * @throws IllegalArgumentException if url is not http/https url
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public void setUrlLessBaseUrl(String baseUrl) {
        Preconditions.check(s -> !URLUtil.isNetworkUrl(s), baseUrl, "baseUrl is not network url");
        Preconditions.check(s -> !s.endsWith("/"), baseUrl, "baseUrl must end in /");
        urlLessBaseUrl = baseUrl;
    }

    /**
     * Check if the installed network restriction applies to the current connection.
     *
     * @return true if the given restriction is limited by the connection
     */
    boolean isRestricted(Context context) {
        final ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetworkInfo = manager.getActiveNetworkInfo();

        if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
            return true;
        }

        if (networkRestriction == NetworkRestriction.NONE) {
            return false;
        }

        final int type = activeNetworkInfo.getType();
        if (type == ConnectivityManager.TYPE_WIFI) {
            return false; // All calls are allowed on Wi-Fi
        }

        final TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        switch (telephonyManager.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return (networkRestriction == NetworkRestriction.MOBILE || networkRestriction == NetworkRestriction.WIFI);
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_LTE:
                // 3G or better
                return networkRestriction == NetworkRestriction.WIFI; // Wi-Fi would be limited
            default:
                // Worse than GPRS, probably EDGE or similar so only None applies
                return true;
        }
    }

    /**
     * The Dispatch mode specifies how and when the SDK will dispatch events.
     */
    public enum DispatchMode {
        /**
         * Automatically dispatch events.
         */
        ONLINE,
        /**
         * Manually dispatch events when dispatch method is called.
         */
        OFFLINE
    }

    /**
     * Restriction in network access. Restrictions are in rising order of connection
     * capability and are treated as a <i>minimum</i> requirement.
     */
    public enum NetworkRestriction {
        /**
         * No restriction applies.
         */
        NONE,
        /**
         * GPRS connection or better is required.
         */
        GPRS,
        /**
         * A mobile connection (3G/4G/LTE) or better is required.
         */
        MOBILE,
        /**
         * A Wi-Fi connection is required.
         */
        WIFI
    }
}
