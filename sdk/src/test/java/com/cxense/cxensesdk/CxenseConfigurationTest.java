package com.cxense.cxensesdk;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-07-04).
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({CxenseSdk.class, TextUtils.class})
public class CxenseConfigurationTest {

    @Before
    public void setUp() throws Exception {
        CxenseSdk cxense = mock(CxenseSdk.class);
        mockStatic(CxenseSdk.class);
        mockStatic(TextUtils.class);
        when(CxenseSdk.getInstance()).thenReturn(cxense);
        when(TextUtils.isEmpty(any(CharSequence.class))).thenAnswer(invocation -> {
            CharSequence arg = invocation.getArgument(0);
            return arg == null || arg.length() == 0;
        });
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getUsername() throws Exception {
        CxenseConfiguration configuration = new CxenseConfiguration();
        assertNull(configuration.getUsername());
    }

    @Test
    public void setUsername() throws Exception {
        CxenseConfiguration configuration = new CxenseConfiguration();
        configuration.setUsername("some_name");
        assertEquals("some_name", Whitebox.getInternalState(configuration, "username"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setUsernameBad() throws Exception {
        CxenseConfiguration configuration = new CxenseConfiguration();
        configuration.setUsername("");
    }

    @Test
    public void getApiKey() throws Exception {
        CxenseConfiguration configuration = new CxenseConfiguration();
        assertNull(configuration.getApiKey());
    }

    @Test
    public void setApiKey() throws Exception {
        CxenseConfiguration configuration = new CxenseConfiguration();
        configuration.setApiKey("some_key");
        assertEquals("some_key", Whitebox.getInternalState(configuration, "apiKey"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setApiKeyBad() throws Exception {
        CxenseConfiguration configuration = new CxenseConfiguration();
        configuration.setApiKey("");
    }

    @Test
    public void isAutoMetaInfoTrackingEnabled() throws Exception {
        CxenseConfiguration configuration = new CxenseConfiguration();
        assertTrue(configuration.isAutoMetaInfoTrackingEnabled());
    }

    @Test
    public void setAutoMetaInfoTrackingEnabled() throws Exception {
        CxenseConfiguration configuration = new CxenseConfiguration();
        configuration.setAutoMetaInfoTrackingEnabled(false);
        assertFalse(Whitebox.getInternalState(configuration, "isAutoMetaInfoTrackingEnabled"));
    }

    @Test
    public void getDispatchMode() throws Exception {
        CxenseConfiguration configuration = new CxenseConfiguration();
        assertEquals(CxenseConfiguration.DispatchMode.ONLINE, configuration.getDispatchMode());
    }

    @Test
    public void setDispatchMode() throws Exception {
        CxenseConfiguration configuration = new CxenseConfiguration();
        configuration.setDispatchMode(CxenseConfiguration.DispatchMode.OFFLINE);
        assertEquals(CxenseConfiguration.DispatchMode.OFFLINE, Whitebox.getInternalState(configuration, "dispatchMode"));
    }

    @Test
    public void getNetworkRestriction() throws Exception {
        CxenseConfiguration configuration = new CxenseConfiguration();
        assertEquals(CxenseConfiguration.NetworkRestriction.NONE, configuration.getNetworkRestriction());
    }

    @Test
    public void setNetworkRestriction() throws Exception {
        CxenseConfiguration configuration = new CxenseConfiguration();
        configuration.setNetworkRestriction(CxenseConfiguration.NetworkRestriction.WIFI);
        assertEquals(CxenseConfiguration.NetworkRestriction.WIFI, Whitebox.getInternalState(configuration, "networkRestriction"));
    }

    @Test
    public void getDispatchPeriod() throws Exception {
        CxenseConfiguration configuration = new CxenseConfiguration();
        assertEquals(CxenseConfiguration.DEFAULT_DISPATCH_PERIOD, configuration.getDispatchPeriod());
    }

    @Test
    public void setDispatchPeriod() throws Exception {
        CxenseConfiguration configuration = new CxenseConfiguration();
        configuration.setDispatchPeriod(CxenseConfiguration.MIN_DISPATCH_PERIOD, TimeUnit.MILLISECONDS);
        long value = Whitebox.getInternalState(configuration, "dispatchPeriod");
        assertEquals(CxenseConfiguration.MIN_DISPATCH_PERIOD, value);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setDispatchPeriodBad() throws Exception {
        CxenseConfiguration configuration = new CxenseConfiguration();
        configuration.setDispatchPeriod(CxenseConfiguration.MIN_DISPATCH_PERIOD - 1, TimeUnit.MILLISECONDS);
    }

    @Test
    public void getOutdatePeriod() throws Exception {
        CxenseConfiguration configuration = new CxenseConfiguration();
        assertEquals(CxenseConfiguration.DEFAULT_OUTDATED_PERIOD, configuration.getOutdatePeriod());
    }

    @Test
    public void setOutdatedPeriod() throws Exception {
        CxenseConfiguration configuration = new CxenseConfiguration();
        configuration.setOutdatedPeriod(CxenseConfiguration.MIN_OUTDATE_PERIOD, TimeUnit.MILLISECONDS);
        long value = Whitebox.getInternalState(configuration, "outdatePeriod");
        assertEquals(CxenseConfiguration.MIN_OUTDATE_PERIOD, value);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setOutdatedPeriodBad() throws Exception {
        CxenseConfiguration configuration = new CxenseConfiguration();
        configuration.setOutdatedPeriod(CxenseConfiguration.MIN_OUTDATE_PERIOD - 1, TimeUnit.MILLISECONDS);
    }

    @Test
    public void isRestricted() throws Exception {
        Context context = mock(Context.class);
        ConnectivityManager manager = mock(ConnectivityManager.class);
        NetworkInfo info = mock(NetworkInfo.class);
        when(context.getSystemService(any(String.class))).thenReturn(manager);
        when(manager.getActiveNetworkInfo()).thenReturn(info);
        when(info.isConnected()).thenReturn(false);
        CxenseConfiguration configuration = new CxenseConfiguration();
        assertTrue(configuration.isRestricted(context));
    }

}