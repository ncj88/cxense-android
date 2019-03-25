package com.cxense.cxensesdk;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.junit.Before;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.reflect.Whitebox;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-07-04).
 */
@PrepareForTest({CxenseConfiguration.class})
public class CxenseConfigurationTest extends BaseTest {
    private CxenseConfiguration configuration;
    private NetworkInfo info;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        info = mock(NetworkInfo.class);
        ConnectivityManager manager = mock(ConnectivityManager.class);
        when(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(manager);
        when(manager.getActiveNetworkInfo()).thenReturn(info);
        configuration = spy(new CxenseConfiguration());
    }

    @Test
    public void getCredentialsProvider() {
        CredentialsProvider credentialsProvider = mock(CredentialsProvider.class);
        Whitebox.setInternalState(configuration, "credentialsProvider", credentialsProvider);
        assertEquals(credentialsProvider, configuration.getCredentialsProvider());
    }

    @Test
    public void setCredentialsProvider() {
        CredentialsProvider credentialsProvider = mock(CredentialsProvider.class);
        configuration.setCredentialsProvider(credentialsProvider);
        assertEquals(credentialsProvider, Whitebox.getInternalState(configuration, "credentialsProvider"));
    }

    @Test
    public void isAutoMetaInfoTrackingEnabled() throws Exception {
        assertTrue(configuration.isAutoMetaInfoTrackingEnabled());
    }

    @Test
    public void setAutoMetaInfoTrackingEnabled() throws Exception {
        configuration.setAutoMetaInfoTrackingEnabled(false);
        assertFalse(Whitebox.getInternalState(configuration, "isAutoMetaInfoTrackingEnabled"));
    }

    @Test
    public void getDispatchMode() throws Exception {
        assertEquals(CxenseConfiguration.DispatchMode.ONLINE, configuration.getDispatchMode());
    }

    @Test
    public void setDispatchMode() throws Exception {
        configuration.setDispatchMode(CxenseConfiguration.DispatchMode.OFFLINE);
        assertEquals(CxenseConfiguration.DispatchMode.OFFLINE,
                Whitebox.getInternalState(configuration, "dispatchMode"));
    }

    @Test
    public void getNetworkRestriction() throws Exception {
        assertEquals(CxenseConfiguration.NetworkStatus.NONE, configuration.getMinimumNetworkStatus());
    }

    @Test
    public void setNetworkRestriction() throws Exception {
        configuration.setMinimumNetworkStatus(CxenseConfiguration.NetworkStatus.WIFI);
        assertEquals(CxenseConfiguration.NetworkStatus.WIFI,
                Whitebox.getInternalState(configuration, "minimumNetworkStatus"));
    }

    @Test
    public void getDispatchPeriod() throws Exception {
        assertEquals(CxenseConstants.DEFAULT_DISPATCH_PERIOD, configuration.getDispatchPeriod());
    }

    @Test
    public void setDispatchPeriod() throws Exception {
        configuration.setDispatchPeriod(CxenseConstants.MIN_DISPATCH_PERIOD, TimeUnit.MILLISECONDS);
        long value = Whitebox.getInternalState(configuration, "dispatchPeriod");
        assertEquals(CxenseConstants.MIN_DISPATCH_PERIOD, value);
    }

    @Test
    public void setDispatchPeriodWithListener() throws Exception {
        CxenseConfiguration.DispatchPeriodListener listener = mock(CxenseConfiguration.DispatchPeriodListener.class);
        configuration.setDispatchPeriodListener(listener);
        configuration.setDispatchPeriod(CxenseConstants.MIN_DISPATCH_PERIOD, TimeUnit.MILLISECONDS);
        long value = Whitebox.getInternalState(configuration, "dispatchPeriod");
        assertEquals(CxenseConstants.MIN_DISPATCH_PERIOD, value);
        verify(listener).onDispatchPeriodChanged(anyLong());
    }

    @Test(expected = IllegalArgumentException.class)
    public void setDispatchPeriodBad() throws Exception {
        configuration.setDispatchPeriod(CxenseConstants.MIN_DISPATCH_PERIOD - 1, TimeUnit.MILLISECONDS);
    }

    @Test
    public void getOutdatePeriod() throws Exception {
        assertEquals(CxenseConstants.DEFAULT_OUTDATED_PERIOD, configuration.getOutdatePeriod());
    }

    @Test
    public void setOutdatedPeriod() throws Exception {
        configuration.setOutdatedPeriod(CxenseConstants.MIN_OUTDATE_PERIOD, TimeUnit.MILLISECONDS);
        long value = Whitebox.getInternalState(configuration, "outdatePeriod");
        assertEquals(CxenseConstants.MIN_OUTDATE_PERIOD, value);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setOutdatedPeriodBad() throws Exception {
        configuration.setOutdatedPeriod(CxenseConstants.MIN_OUTDATE_PERIOD - 1, TimeUnit.MILLISECONDS);
    }

    @Test
    public void setConsentOptions() {
        configuration.setConsentOptions(ConsentOption.CONSENT_REQUIRED);
        Set<ConsentOption> consentOptions = Whitebox.getInternalState(configuration, "consentOptions");
        assertEquals(1, consentOptions.size());
        assertTrue(consentOptions.contains(ConsentOption.CONSENT_REQUIRED));
    }

    @Test
    public void getConsentOptionsValues() {
        Whitebox.setInternalState(configuration, "consentOptions", new HashSet<>(Collections.singletonList(ConsentOption.CONSENT_REQUIRED)));
        assertThat(configuration.getConsentOptionsValues(), is(Collections.singletonList(ConsentOption.CONSENT_REQUIRED.getValue())));
    }

    @Test
    public void getConsentOptionsAsStringEmptyOptions() {
        assertNull(configuration.getConsentOptionsAsString());
    }

    //    @Test
//    public void isRestricted() throws Exception {
//        when(info.isConnected()).thenReturn(false);
//        assertTrue(configuration.isRestricted(context));
//    }
//
//    @Test
//    public void isRestrictedNone() throws Exception {
//        when(info.isConnected()).thenReturn(true);
//        Whitebox.setInternalState(configuration, "networkRestriction", CxenseConfiguration.NetworkStatus.NONE);
//        assertFalse(configuration.isRestricted(context));
//    }
//
//    @Test
//    public void isRestrictedOnWifi() throws Exception {
//        when(info.isConnected()).thenReturn(true);
//        when(info.getType()).thenReturn(ConnectivityManager.TYPE_WIFI);
//        Whitebox.setInternalState(configuration, "networkRestriction", CxenseConfiguration.NetworkStatus.MOBILE);
//        assertFalse(configuration.isRestricted(context));
//    }
//
//    @Test
//    public void isRestrictedOnGPRS() throws Exception {
//        when(info.isConnected()).thenReturn(true);
//        Whitebox.setInternalState(configuration, "networkRestriction", CxenseConfiguration.NetworkStatus.MOBILE);
//        assertTrue(checkRestrictionForNetwork(TelephonyManager.NETWORK_TYPE_GPRS));
//    }
//
//    @Test
//    public void isRestrictedOn3gOrBetter() throws Exception {
//        when(info.isConnected()).thenReturn(true);
//        Whitebox.setInternalState(configuration, "networkRestriction", CxenseConfiguration.NetworkStatus.WIFI);
//        assertThat(true, allOf(
//                is(checkRestrictionForNetwork(TelephonyManager.NETWORK_TYPE_HSDPA)),
//                is(checkRestrictionForNetwork(TelephonyManager.NETWORK_TYPE_HSPA)),
//                is(checkRestrictionForNetwork(TelephonyManager.NETWORK_TYPE_HSPAP)),
//                is(checkRestrictionForNetwork(TelephonyManager.NETWORK_TYPE_HSUPA)),
//                is(checkRestrictionForNetwork(TelephonyManager.NETWORK_TYPE_LTE))
//        ));
//    }
//
//    @Test
//    public void isRestrictedOnUnknown() throws Exception {
//        when(info.isConnected()).thenReturn(true);
//        Whitebox.setInternalState(configuration, "networkRestriction", CxenseConfiguration.NetworkStatus.MOBILE);
//        assertTrue(checkRestrictionForNetwork(TelephonyManager.NETWORK_TYPE_EDGE));
//    }
//
//    private boolean checkRestrictionForNetwork(int networkType) {
//        TelephonyManager telephonyManager = mock(TelephonyManager.class);
//        when(telephonyManager.getNetworkType()).thenReturn(networkType);
//        when(context.getSystemService(Context.TELEPHONY_SERVICE)).thenReturn(telephonyManager);
//        return configuration.isRestricted(context);
//    }

}