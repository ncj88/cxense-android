package com.cxense.cxensesdk;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Cxense SDK configuration class
 *
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-07-03).
 */

public final class CxenseConfiguration {
    static final long DISPATCH_INITIAL_DELAY = TimeUnit.SECONDS.toMillis(30);
    private boolean isAutoMetaInfoTrackingEnabled = true;
    private long dispatchPeriod = CxenseConstants.DEFAULT_DISPATCH_PERIOD;
    private NetworkStatus minimumNetworkStatus = NetworkStatus.NONE;
    private DispatchMode dispatchMode = DispatchMode.ONLINE;
    private long outdatePeriod = CxenseConstants.DEFAULT_OUTDATED_PERIOD;
    private CredentialsProvider credentialsProvider;
    private Set<ConsentOption> consentOptions = new HashSet<>();
    private DispatchPeriodListener dispatchPeriodListener;

    CxenseConfiguration() {
    }

    /**
     * Gets credential provider
     *
     * @return {@link CredentialsProvider} instance
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    @NonNull
    public CredentialsProvider getCredentialsProvider() {
        return credentialsProvider;
    }

    /**
     * Sets credential provider, which provide username/api key dynamically
     *
     * @param credentialsProvider {@link CredentialsProvider} instance
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public void setCredentialsProvider(@NonNull CredentialsProvider credentialsProvider) {
        Preconditions.checkForNull(credentialsProvider, "credentialsProvider");
        this.credentialsProvider = credentialsProvider;
    }

    void setDispatchPeriodListener(DispatchPeriodListener listener) {
        dispatchPeriodListener = listener;
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
     * Returns the minimum network status for sending events
     *
     * @return the minimum network status
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public NetworkStatus getMinimumNetworkStatus() {
        return minimumNetworkStatus;
    }

    /**
     * Set the minimum network status which should apply for automatic dispatching
     * of events. The default value is #NetworkStatus.NONE
     *
     * @param status the minimum network status which should be used
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public void setMinimumNetworkStatus(NetworkStatus status) {
        minimumNetworkStatus = status;
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
     * @throws IllegalArgumentException if period smaller then CxenseConstants.MIN_DISPATCH_PERIOD
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public void setDispatchPeriod(long period, TimeUnit unit) {
        long millis = unit.toMillis(period);
        if (millis < CxenseConstants.MIN_DISPATCH_PERIOD)
            throw new IllegalArgumentException(String.format(Locale.US, "period must be greater than %d seconds",
                    TimeUnit.MILLISECONDS.toSeconds(CxenseConstants.MIN_DISPATCH_PERIOD)));
        if (dispatchPeriod != millis) {
            dispatchPeriod = millis;
            if (dispatchPeriodListener != null)
                dispatchPeriodListener.onDispatchPeriodChanged(dispatchPeriod);
        }
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
     * @throws IllegalArgumentException if period smaller than CxenseConstants.MIN_DISPATCH_PERIOD
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public void setOutdatedPeriod(long period, TimeUnit unit) {
        long millis = unit.toMillis(period);
        if (millis < CxenseConstants.MIN_OUTDATE_PERIOD)
            throw new IllegalArgumentException(String.format(Locale.US, "period must be greater than %d seconds",
                    TimeUnit.MILLISECONDS.toSeconds(CxenseConstants.MIN_OUTDATE_PERIOD)));
        this.outdatePeriod = millis;
    }

    /**
     * Returns current consent options for user
     *
     * @return current consent options
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    @NonNull
    public Set<ConsentOption> getConsentOptions() {
        return Collections.unmodifiableSet(consentOptions);
    }

    /**
     * Set consent options for user data
     *
     * @param options new options
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public void setConsentOptions(@NonNull ConsentOption... options) {
        consentOptions = new HashSet<>(Arrays.asList(options));
    }

    /**
     * Returns current consent options for user as string values
     *
     * @return current consent options string values
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    @NonNull
    public List<String> getConsentOptionsValues() {
        List<String> values = new ArrayList<>();
        for (ConsentOption option : consentOptions) {
            values.add(option.getValue());
        }
        return values;
    }

    /**
     * Returns current consent options for user as comma-delimited string
     *
     * @return comma-delimited string with current consent options
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    @Nullable
    public String getConsentOptionsAsString() {
        if (consentOptions.isEmpty())
            return null;
        return TextUtils.join(",", getConsentOptionsValues());
    }

    /**
     * Checks, that we use username and api key or persistent id
     *
     * @return True, if username and api key filled
     */
    boolean isApiCredentialsProvided() {
        return !TextUtils.isEmpty(credentialsProvider.getUsername()) && !TextUtils.isEmpty(credentialsProvider.getApiKey());
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
         * Don't dispatch events, only store it locally.
         */
        OFFLINE
    }

    /**
     * Network statuses ordered by connection capability.
     */
    public enum NetworkStatus {
        /**
         * No network.
         */
        NONE,
        /**
         * GPRS connection.
         */
        GPRS,
        /**
         * A mobile connection (3G/4G/LTE).
         */
        MOBILE,
        /**
         * A Wi-Fi connection.
         */
        WIFI
    }

    interface DispatchPeriodListener {
        void onDispatchPeriodChanged(long millis);
    }
}
