package com.cxense.cxensesdk;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
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

    @Before
    public void setUp() throws Exception {
        super.setUp();
        deviceInfoProvider = new DeviceInfoProvider(context);
        pm = mock(PackageManager.class);
        when(context.getPackageManager()).thenReturn(pm);
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
    public void getCurrentNetworkStatus() {
    }
}