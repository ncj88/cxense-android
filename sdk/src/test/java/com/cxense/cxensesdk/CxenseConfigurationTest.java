package com.cxense.cxensesdk;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-07-04).
 */
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
        configuration = new CxenseConfiguration();
    }

    @Test
    public void getUsername() throws Exception {
        assertNull(configuration.getUsername());
    }

    @Test
    public void setUsername() throws Exception {
        configuration.setUsername("some_name");
        assertEquals("some_name", Whitebox.getInternalState(configuration, "username"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setUsernameBad() throws Exception {
        configuration.setUsername("");
    }

    @Test
    public void getApiKey() throws Exception {
        assertNull(configuration.getApiKey());
    }

    @Test
    public void setApiKey() throws Exception {
        configuration.setApiKey("some_key");
        assertEquals("some_key", Whitebox.getInternalState(configuration, "apiKey"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setApiKeyBad() throws Exception {
        configuration.setApiKey("");
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
        assertEquals(CxenseConfiguration.DispatchMode.OFFLINE, Whitebox.getInternalState(configuration, "dispatchMode"));
    }

    @Test
    public void getNetworkRestriction() throws Exception {
        assertEquals(CxenseConfiguration.NetworkRestriction.NONE, configuration.getNetworkRestriction());
    }

    @Test
    public void setNetworkRestriction() throws Exception {
        configuration.setNetworkRestriction(CxenseConfiguration.NetworkRestriction.WIFI);
        assertEquals(CxenseConfiguration.NetworkRestriction.WIFI, Whitebox.getInternalState(configuration, "networkRestriction"));
    }

    @Test
    public void getDispatchPeriod() throws Exception {
        assertEquals(CxenseConfiguration.DEFAULT_DISPATCH_PERIOD, configuration.getDispatchPeriod());
    }

    @Test
    public void setDispatchPeriod() throws Exception {
        configuration.setDispatchPeriod(CxenseConfiguration.MIN_DISPATCH_PERIOD, TimeUnit.MILLISECONDS);
        long value = Whitebox.getInternalState(configuration, "dispatchPeriod");
        assertEquals(CxenseConfiguration.MIN_DISPATCH_PERIOD, value);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setDispatchPeriodBad() throws Exception {
        configuration.setDispatchPeriod(CxenseConfiguration.MIN_DISPATCH_PERIOD - 1, TimeUnit.MILLISECONDS);
    }

    @Test
    public void getOutdatePeriod() throws Exception {
        assertEquals(CxenseConfiguration.DEFAULT_OUTDATED_PERIOD, configuration.getOutdatePeriod());
    }

    @Test
    public void setOutdatedPeriod() throws Exception {
        configuration.setOutdatedPeriod(CxenseConfiguration.MIN_OUTDATE_PERIOD, TimeUnit.MILLISECONDS);
        long value = Whitebox.getInternalState(configuration, "outdatePeriod");
        assertEquals(CxenseConfiguration.MIN_OUTDATE_PERIOD, value);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setOutdatedPeriodBad() throws Exception {
        configuration.setOutdatedPeriod(CxenseConfiguration.MIN_OUTDATE_PERIOD - 1, TimeUnit.MILLISECONDS);
    }

    @Test
    public void isRestricted() throws Exception {
        when(info.isConnected()).thenReturn(false);
        assertTrue(configuration.isRestricted(context));
    }

    @Test
    public void isRestrictedNone() throws Exception {
        when(info.isConnected()).thenReturn(true);
        Whitebox.setInternalState(configuration, "networkRestriction", CxenseConfiguration.NetworkRestriction.NONE);
        assertFalse(configuration.isRestricted(context));
    }

    @Test
    public void isRestrictedOnWifi() throws Exception {
        when(info.isConnected()).thenReturn(true);
        when(info.getType()).thenReturn(ConnectivityManager.TYPE_WIFI);
        Whitebox.setInternalState(configuration, "networkRestriction", CxenseConfiguration.NetworkRestriction.MOBILE);
        assertFalse(configuration.isRestricted(context));
    }

    @Test
    public void isRestrictedOnGPRS() throws Exception {
        when(info.isConnected()).thenReturn(true);
        Whitebox.setInternalState(configuration, "networkRestriction", CxenseConfiguration.NetworkRestriction.MOBILE);
        assertTrue(checkRestrictionForNetwork(TelephonyManager.NETWORK_TYPE_GPRS));
    }

    @Test
    public void isRestrictedOn3gOrBetter() throws Exception {
        when(info.isConnected()).thenReturn(true);
        Whitebox.setInternalState(configuration, "networkRestriction", CxenseConfiguration.NetworkRestriction.WIFI);
        assertThat(true, allOf(
                is(checkRestrictionForNetwork(TelephonyManager.NETWORK_TYPE_HSDPA)),
                is(checkRestrictionForNetwork(TelephonyManager.NETWORK_TYPE_HSPA)),
                is(checkRestrictionForNetwork(TelephonyManager.NETWORK_TYPE_HSPAP)),
                is(checkRestrictionForNetwork(TelephonyManager.NETWORK_TYPE_HSUPA)),
                is(checkRestrictionForNetwork(TelephonyManager.NETWORK_TYPE_LTE))
        ));
    }

    @Test
    public void isRestrictedOnUnknown() throws Exception {
        when(info.isConnected()).thenReturn(true);
        Whitebox.setInternalState(configuration, "networkRestriction", CxenseConfiguration.NetworkRestriction.MOBILE);
        assertTrue(checkRestrictionForNetwork(TelephonyManager.NETWORK_TYPE_EDGE));
    }

    private boolean checkRestrictionForNetwork(int networkType) {
        TelephonyManager telephonyManager = mock(TelephonyManager.class);
        when(telephonyManager.getNetworkType()).thenReturn(networkType);
        when(context.getSystemService(Context.TELEPHONY_SERVICE)).thenReturn(telephonyManager);
        return configuration.isRestricted(context);
    }

}