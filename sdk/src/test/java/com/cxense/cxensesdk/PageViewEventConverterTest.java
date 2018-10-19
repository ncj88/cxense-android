package com.cxense.cxensesdk;

import android.location.Location;
import android.util.DisplayMetrics;

import com.cxense.cxensesdk.model.ExternalUserId;
import com.cxense.cxensesdk.model.PageViewEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasKey;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2018-10-15).
 */
@PrepareForTest({PageViewEvent.class, CxenseConfiguration.class, ExternalUserId.class})
public class PageViewEventConverterTest extends BaseTest {
    private static final String APPNAME = "NAME";
    private static final String APPVERSION = "1.0";
    private ObjectMapper mapper;
    private PageViewEventConverter converter;
    private PageViewEvent event;
    private DeviceInfoProvider deviceInfoProvider;
    private CxenseConfiguration configuration;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        event = mock(PageViewEvent.class);
        mapper = mock(ObjectMapper.class);
        deviceInfoProvider = mock(DeviceInfoProvider.class);
        configuration = mock(CxenseConfiguration.class);
        converter = spy(new PageViewEventConverter(mapper, configuration, deviceInfoProvider));
        when(event.getDate()).thenReturn(new Date(0));
    }

    @Test
    public void canConvert() {
        assertTrue(converter.canConvert(event));
    }

    @Test
    public void toQueryMap() {
        doReturn(true).when(configuration).isAutoMetaInfoTrackingEnabled();
        doReturn(APPNAME).when(deviceInfoProvider).getApplicationName();
        doReturn(APPVERSION).when(deviceInfoProvider).getApplicationVersion();
        DisplayMetrics dm = new DisplayMetrics();
        when(deviceInfoProvider.getDisplayMetrics()).thenReturn(dm);
        Location userLocation = mock(Location.class);
        when(userLocation.hasAccuracy()).thenReturn(true);
        when(userLocation.hasAltitude()).thenReturn(true);
        when(userLocation.hasBearing()).thenReturn(true);
        when(userLocation.hasSpeed()).thenReturn(true);
        when(event.getUserLocation()).thenReturn(userLocation);
        when(event.getExternalUserIds()).thenReturn(Collections.singletonList(mock(ExternalUserId.class)));
        Map<String, String> params = new HashMap<>();
        params.put("key", "value");
        when(event.getCustomParameters()).thenReturn(params);
        when(event.getCustomUserParameters()).thenReturn(params);
        Map<String, String> result = converter.toQueryMap(event);
        assertThat(result, allOf(
                hasKey(PageViewEventConverter.SITE_ID),
                hasKey(PageViewEventConverter.VERSION),
                hasKey(PageViewEventConverter.TYPE),
                hasKey(PageViewEventConverter.LOCATION),
                hasKey(PageViewEventConverter.REFERRER),
                hasKey(PageViewEventConverter.TIME),
                hasKey(PageViewEventConverter.TIME_OFFSET),
                hasKey(PageViewEventConverter.RND),
                hasKey(PageViewEventConverter.CKP),
                hasKey(PageViewEventConverter.ENCODING),
                hasEntry(PageViewEventConverter.CUSTOM_PARAMETER_PREFIX + "app", APPNAME),
                hasEntry(PageViewEventConverter.CUSTOM_PARAMETER_PREFIX + "appv", APPVERSION)
        ));
        verify(configuration).isAutoMetaInfoTrackingEnabled();
        verify(deviceInfoProvider).getApplicationName();
        verify(deviceInfoProvider).getApplicationVersion();
    }

    @Test
    public void toEventRecord() throws Exception {
        Map<String, String> eventMap = new HashMap<>();
        eventMap.put(PageViewEventConverter.CKP, "ckp");
        eventMap.put(PageViewEventConverter.RND, "rnd");
        doReturn(eventMap).when(converter).toQueryMap(event);
        assertNotNull(converter.toEventRecord(event));
        verify(converter).toQueryMap(event);
        verify(mapper).writeValueAsString(any());
    }
}