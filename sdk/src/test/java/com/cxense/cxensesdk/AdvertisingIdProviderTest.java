package com.cxense.cxensesdk;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;

import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import java.util.concurrent.ScheduledExecutorService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.mock;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2018-10-17).
 */
public class AdvertisingIdProviderTest extends BaseTest {
    AdvertisingIdProvider advertisingIdProvider;

    @Before
    public void setUp() throws Exception {
        advertisingIdProvider = new AdvertisingIdProvider(context, mock(ScheduledExecutorService.class));
    }

    private void prepareAdvertisingInfo(AdvertisingIdClient.Info info) {
        Whitebox.setInternalState(advertisingIdProvider, "advertisingInfo", info);
    }

    @Test
    public void getDefaultUserId() {
        prepareAdvertisingInfo(new AdvertisingIdClient.Info("id", true));
        assertEquals("id", advertisingIdProvider.getDefaultUserId());
    }

    @Test
    public void getDefaultUserIdNullInfo() {
        prepareAdvertisingInfo(null);
        assertNull(advertisingIdProvider.getDefaultUserId());
    }

    @Test
    public void isLimitAdTrackingEnabled() {
        prepareAdvertisingInfo(new AdvertisingIdClient.Info("id", true));
        assertTrue(advertisingIdProvider.isLimitAdTrackingEnabled());
    }

    @Test
    public void isLimitAdTrackingEnabledNullInfo() {
        prepareAdvertisingInfo(null);
        assertFalse(advertisingIdProvider.isLimitAdTrackingEnabled());
    }
}