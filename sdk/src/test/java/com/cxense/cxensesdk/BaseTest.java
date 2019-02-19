package com.cxense.cxensesdk;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.URLUtil;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-07-19).
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({TextUtils.class, Log.class, URLUtil.class})
@PowerMockIgnore("javax.net.ssl.*")
public abstract class BaseTest {
    protected Context context;

    // Extracted from Android URLUtil code start
    private static boolean isHttpUrl(String url) {
        return (null != url) &&
                (url.length() > 6) &&
                url.substring(0, 7).equalsIgnoreCase("http://");
    }

    private static boolean isHttpsUrl(String url) {
        return (null != url) &&
                (url.length() > 7) &&
                url.substring(0, 8).equalsIgnoreCase("https://");
    }

    private static boolean isNetworkUrl(String url) {
        if (url == null || url.length() == 0) {
            return false;
        }
        return isHttpUrl(url) || isHttpsUrl(url);
    }

    @Before
    public void setUp() throws Exception {
        context = mock(Context.class);
        when(context.getApplicationContext()).thenReturn(context);
        mockStatic(TextUtils.class, Log.class, URLUtil.class);
        when(URLUtil.isNetworkUrl(anyString())).thenAnswer(invocation -> isNetworkUrl(invocation.getArgument(0)));
        when(TextUtils.isEmpty(any())).thenAnswer(invocation -> {
            CharSequence arg = invocation.getArgument(0);
            return arg == null || arg.length() == 0;
        });
    }
}
