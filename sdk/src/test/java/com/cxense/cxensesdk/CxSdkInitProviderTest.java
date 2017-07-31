package com.cxense.cxensesdk;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-07-31).
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({CxenseSdk.class})
public class CxSdkInitProviderTest {
    private CxSdkInitProvider provider;

    @Before
    public void setUp() throws Exception {
        mockStatic(CxenseSdk.class);
        provider = new CxSdkInitProvider();
    }

    @Test
    public void initCxense() throws Exception {
        Context context = mock(Context.class);
        when(context.getApplicationContext()).thenReturn(context);
        provider.initCxense(context);
    }

    @Test
    public void getAuthority() throws Exception {
        assertEquals(BuildConfig.AUTHORITY, provider.getAuthority());
    }

}