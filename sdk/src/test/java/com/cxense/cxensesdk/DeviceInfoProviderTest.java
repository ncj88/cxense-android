package com.cxense.cxensesdk;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2018-10-18).
 */
public class DeviceInfoProviderTest extends BaseTest {
    private DeviceInfoProvider deviceInfoProvider;
    private PackageManager pm;
    private NetworkInfo info;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        deviceInfoProvider = new DeviceInfoProvider(context);
        pm = mock(PackageManager.class);
        when(context.getPackageManager()).thenReturn(pm);
        info = mock(NetworkInfo.class);
        ConnectivityManager manager = mock(ConnectivityManager.class);
        when(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(manager);
        when(manager.getActiveNetworkInfo()).thenReturn(info);
    }

    @Test
    public void getDisplayMetrics() {
        DisplayMetrics dm = mock(DisplayMetrics.class);
        Resources resources = mock(Resources.class);
        when(context.getResources()).thenReturn(resources);
        when(resources.getDisplayMetrics()).thenReturn(dm);
        assertEquals(dm, deviceInfoProvider.getDisplayMetrics());
    }

    @Test
    public void getApplicationVersion() throws Exception {
        PackageInfo pi = new PackageInfo();
        pi.versionName = "1234";
        when(pm.getPackageInfo((String) any(), anyInt())).thenReturn(pi);
        assertEquals("1234", deviceInfoProvider.getApplicationVersion());
    }

    @Test
    public void getApplicationVersionException() throws Exception {
        when(pm.getPackageInfo((String) any(), anyInt())).thenThrow(new PackageManager.NameNotFoundException());
        assertNull(deviceInfoProvider.getApplicationVersion());
    }

    @Test
    public void getApplicationName() {
        when(pm.getApplicationLabel(any())).thenReturn("test");
        assertEquals("test", deviceInfoProvider.getApplicationName());
    }


    @Test
    public void getApplicationNameNull() {
        when(pm.getApplicationLabel(any(ApplicationInfo.class))).thenReturn(null);
        assertNull(deviceInfoProvider.getApplicationName());
    }

    @Test
    public void getCurrentNetworkStatusNotConnected() {
        when(info.isConnected()).thenReturn(false);
        assertEquals(CxenseConfiguration.NetworkStatus.NONE, deviceInfoProvider.getCurrentNetworkStatus());
    }

    @Test
    public void getCurrentNetworkStatusWifiNetwork() {
        when(info.isConnected()).thenReturn(true);
        when(info.getType()).thenReturn(ConnectivityManager.TYPE_WIFI);
        assertEquals(CxenseConfiguration.NetworkStatus.WIFI, deviceInfoProvider.getCurrentNetworkStatus());
    }

    @Test
    public void getCurrentNetworkStatusMobile() {
        when(info.isConnected()).thenReturn(true);
        assertThat(CxenseConfiguration.NetworkStatus.MOBILE, allOf(
                is(getNetworkStatus(TelephonyManager.NETWORK_TYPE_HSDPA)),
                is(getNetworkStatus(TelephonyManager.NETWORK_TYPE_HSPA)),
                is(getNetworkStatus(TelephonyManager.NETWORK_TYPE_HSPAP)),
                is(getNetworkStatus(TelephonyManager.NETWORK_TYPE_HSUPA)),
                is(getNetworkStatus(TelephonyManager.NETWORK_TYPE_LTE))
        ));
        assertEquals(CxenseConfiguration.NetworkStatus.GPRS, getNetworkStatus(TelephonyManager.NETWORK_TYPE_EDGE));
    }

    private CxenseConfiguration.NetworkStatus getNetworkStatus(int networkType) {
        TelephonyManager telephonyManager = mock(TelephonyManager.class);
        when(telephonyManager.getNetworkType()).thenReturn(networkType);
        when(context.getSystemService(Context.TELEPHONY_SERVICE)).thenReturn(telephonyManager);
        return deviceInfoProvider.getCurrentNetworkStatus();
    }
}