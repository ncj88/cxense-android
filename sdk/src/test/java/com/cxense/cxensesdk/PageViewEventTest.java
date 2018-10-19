package com.cxense.cxensesdk;

import android.location.Location;

import com.cxense.cxensesdk.model.PageViewEvent;

import org.junit.Before;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.reflect.Whitebox;

import java.util.Date;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.spy;

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
        DependenciesProvider.init(context);
        configuration = spy(new CxenseConfiguration());
//        when(cxense.getConfiguration()).thenReturn(configuration);
        event = spy(new PageViewEvent.Builder("siteId").setLocation("http://example.com").build());
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

}