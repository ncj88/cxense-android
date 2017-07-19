package com.cxense.cxensesdk;

import android.text.TextUtils;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-07-19).
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({CxenseSdk.class, TextUtils.class})
public abstract class BaseTest {
    protected static final String APPNAME = "NAME";
    protected static final String APPVERSION = "1.0";
    protected CxenseSdk cxense;

    @Before
    public void setUp() throws Exception {
        mockStatic(TextUtils.class);
        initCxenseSdk();
        doReturn(APPNAME).when(cxense).getApplicationName();
        doReturn(APPVERSION).when(cxense).getApplicationVersion();
        when(TextUtils.isEmpty(any())).thenAnswer(invocation -> {
            CharSequence arg = invocation.getArgument(0);
            return arg == null || arg.length() == 0;
        });
    }

    protected void initCxenseSdk() throws Exception {
        mockStatic(CxenseSdk.class);
        cxense = mock(CxenseSdk.class);
        when(CxenseSdk.getInstance()).thenReturn(cxense);
        when(cxense.packObject(any())).thenReturn("{}");
    }
}
