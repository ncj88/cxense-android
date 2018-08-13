package com.cxense.cxensesdk;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ProviderInfo;
import android.net.Uri;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-07-31).
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({CxenseSdk.class})
public class CxSdkInitProviderTest {
    private Context context;
    private CxSdkInitProvider provider;
    private Uri uri;

    @Before
    public void setUp() throws Exception {
        context = mock(Context.class);
        mockStatic(CxenseSdk.class);
        provider = spy(new CxSdkInitProvider());
        uri = mock(Uri.class);
        doReturn(context).when(provider).getContext();
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

    @Test
    public void onCreate() throws Exception {
        doNothing().when(provider).initCxense(any(Context.class));
        assertFalse(provider.onCreate());
        verify(provider).getContext();
        verify(provider).initCxense(context);
    }

    @Test
    public void checkAttachInfo() throws Exception {
        ProviderInfo info = new ProviderInfo();
        info.authority = "TEST";
        doReturn("AUTHORITY").when(provider).getAuthority();
        provider.checkAttachInfo(info);
    }

    @Test(expected = NullPointerException.class)
    public void checkAttachInfooNull() throws Exception {
        provider.checkAttachInfo(null);
    }

    @Test(expected = IllegalStateException.class)
    public void checkAttachInfoNotValidAuthority() throws Exception {
        ProviderInfo info = new ProviderInfo();
        info.authority = "TEST";
        doReturn(info.authority).when(provider).getAuthority();
        provider.checkAttachInfo(info);
    }

    @Test
    public void query() throws Exception {
        assertNull(provider.query(uri, null, null, null, null));
    }

    @Test
    public void getType() throws Exception {
        assertNull(provider.getType(uri));
    }

    @Test
    public void insert() throws Exception {
        assertNull(provider.insert(uri, mock(ContentValues.class)));
    }

    @Test
    public void delete() throws Exception {
        assertEquals(0, provider.delete(uri, null, null));
    }

    @Test
    public void update() throws Exception {
        assertEquals(0, provider.update(uri, mock(ContentValues.class), null, null));
    }

}