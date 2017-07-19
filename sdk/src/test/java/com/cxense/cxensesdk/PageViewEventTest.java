package com.cxense.cxensesdk;

import android.util.DisplayMetrics;

import org.junit.Before;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.util.HashMap;
import java.util.Map;

import static com.cxense.cxensesdk.PageViewEvent.CUSTOM_PARAMETER_PREFIX;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasKey;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-07-19).
 */
@PrepareForTest({CxenseConfiguration.class, PageViewEvent.class})
public class PageViewEventTest extends BaseTest {
    private PageViewEvent event;
    private CxenseConfiguration configuration;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        configuration = spy(new CxenseConfiguration());
        when(cxense.getConfiguration()).thenReturn(configuration);
        event = spy(new PageViewEvent.Builder("siteId", "http://example.com").build());
    }

    @Test
    public void toEventRecord() throws Exception {
        Map<String, String> eventMap = new HashMap<>();
        eventMap.put(PageViewEvent.CKP, "ckp");
        eventMap.put(PageViewEvent.RND, "rnd");
        doReturn(eventMap).when(event).toMap();
        assertNotNull(event.toEventRecord());
        verify(event).toMap();
        verify(cxense).packObject(any());
    }

    @Test
    public void toMap() throws Exception {
        DisplayMetrics dm = new DisplayMetrics();
        when(cxense.getDisplayMetrics()).thenReturn(dm);
        Map<String, String> result = event.toMap();
        assertThat(result, allOf(
                hasKey(PageViewEvent.SITE_ID),
                hasKey(PageViewEvent.VERSION),
                hasKey(PageViewEvent.TYPE),
                hasKey(PageViewEvent.LOCATION),
                hasKey(PageViewEvent.REFERRER),
                hasKey(PageViewEvent.TIME),
                hasKey(PageViewEvent.TIME_OFFSET),
                hasKey(PageViewEvent.RND),
                hasKey(PageViewEvent.CKP),
                hasKey(PageViewEvent.ENCODING),
                hasEntry(CUSTOM_PARAMETER_PREFIX + "app", APPNAME),
                hasEntry(CUSTOM_PARAMETER_PREFIX + "appv", APPVERSION)
        ));
        verify(configuration).isAutoMetaInfoTrackingEnabled();
        verify(cxense).getApplicationName();
        verify(cxense).getApplicationVersion();
    }

}