package com.cxense.cxensesdk;

import android.location.Location;
import android.util.DisplayMetrics;

import org.junit.Before;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.reflect.Whitebox;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.cxense.cxensesdk.PageViewEvent.CUSTOM_PARAMETER_PREFIX;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
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
        event = spy(new PageViewEvent.Builder("siteId").setLocation("http://example.com").build());
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
    public void getEventId() throws Exception {
        String eventId = "eventId";
        Whitebox.setInternalState(event, "eventId", eventId);
        assertEquals(eventId, event.getEventId());
    }

    @Test
    public void getType() throws Exception {
        String type = "type";
        Whitebox.setInternalState(event, "type", type);
        assertEquals(type, event.getType());
    }

    @Test
    public void getAccountId() throws Exception {
        int accountId = 1;
        Whitebox.setInternalState(event, "accountId", accountId);
        assertEquals(accountId, event.getAccountId());
    }

    @Test
    public void getSiteId() throws Exception {
        String siteId = "siteId";
        Whitebox.setInternalState(event, "siteId", siteId);
        assertEquals(siteId, event.getSiteId());
    }

    @Test
    public void getContentId() throws Exception {
        String contentId = "contentId";
        Whitebox.setInternalState(event, "contentId", contentId);
        assertEquals(contentId, event.getContentId());
    }

    @Test
    public void getLocation() throws Exception {
        String location = "location";
        Whitebox.setInternalState(event, "location", location);
        assertEquals(location, event.getLocation());
    }

    @Test
    public void getReferrer() throws Exception {
        String referrer = "referrer";
        Whitebox.setInternalState(event, "referrer", referrer);
        assertEquals(referrer, event.getReferrer());
    }

    @Test
    public void getGoalId() throws Exception {
        String goalId = "goalId";
        Whitebox.setInternalState(event, "goalId", goalId);
        assertEquals(goalId, event.getGoalId());
    }

    @Test
    public void getPageName() throws Exception {
        String pageName = "pageName";
        Whitebox.setInternalState(event, "pageName", pageName);
        assertEquals(pageName, event.getPageName());
    }

    @Test
    public void getDate() throws Exception {
        Date date = new Date();
        Whitebox.setInternalState(event, "date", date);
        assertEquals(date, event.getDate());
    }

    @Test
    public void isNewUser() throws Exception {
        Whitebox.setInternalState(event, "isNewUser", true);
        assertTrue(event.isNewUser());
    }

    @Test
    public void getUserLocation() throws Exception {
        Location userLocation = new Location("gps");
        Whitebox.setInternalState(event, "userLocation", userLocation);
        assertThat(userLocation, is(event.getUserLocation()));
    }

    @Test
    public void toMap() throws Exception {
        doReturn(APPNAME).when(cxense).getApplicationName();
        doReturn(APPVERSION).when(cxense).getApplicationVersion();
        DisplayMetrics dm = new DisplayMetrics();
        when(cxense.getDisplayMetrics()).thenReturn(dm);
        Location userLocation = mock(Location.class);
        when(userLocation.hasAccuracy()).thenReturn(true);
        when(userLocation.hasAltitude()).thenReturn(true);
        when(userLocation.hasBearing()).thenReturn(true);
        when(userLocation.hasSpeed()).thenReturn(true);
        Map<String, String> params = new HashMap<>();
        params.put("key", "value");
        List<ExternalUserId> externalUserIds = new ArrayList<>();
        externalUserIds.add(new ExternalUserId("userType", "userId"));
        Whitebox.setInternalState(event, "userLocation", userLocation);
        Whitebox.setInternalState(event, "customParameters", params);
        Whitebox.setInternalState(event, "customUserParameters", params);
        Whitebox.setInternalState(event, "externalUserIds", externalUserIds);
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