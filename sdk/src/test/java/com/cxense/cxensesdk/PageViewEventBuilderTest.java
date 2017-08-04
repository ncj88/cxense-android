package com.cxense.cxensesdk;

import android.location.Location;

import com.cxense.ArrayFixedSizeQueue;

import org.junit.Before;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.reflect.Whitebox;

import java.util.Arrays;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-07-31).
 */
@PrepareForTest({PageViewEvent.class, CxenseConfiguration.class})
public class PageViewEventBuilderTest extends BaseTest {
    private PageViewEvent event;
    private PageViewEvent.Builder builder;
    private CxenseConfiguration configuration;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        event = mock(PageViewEvent.class);
        configuration = spy(new CxenseConfiguration());
        when(cxense.getConfiguration()).thenReturn(configuration);
        builder = new PageViewEvent.Builder("siteId");
        whenNew(PageViewEvent.class).withAnyArguments().thenReturn(event);
    }

    @Test
    public void setEventId() throws Exception {
        String eventId = "eventId";
        assertThat(builder, is(builder.setEventId(eventId)));
        assertEquals(eventId, Whitebox.getInternalState(builder, "eventId"));
    }

    @Test
    public void setType() throws Exception {
        String type = "type";
        assertThat(builder, is(builder.setType(type)));
        assertEquals(type, Whitebox.getInternalState(builder, "type"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setTypeNull() throws Exception {
        builder.setType(null);
    }

    @Test
    public void setAccountId() throws Exception {
        int accountId = 1;
        assertThat(builder, is(builder.setAccountId(accountId)));
        assertEquals(accountId, (int) Whitebox.getInternalState(builder, "accountId"));
    }

    @Test
    public void setContentId() throws Exception {
        String contentId = "contentId";
        assertThat(builder, is(builder.setContentId(contentId)));
        assertEquals(contentId, Whitebox.getInternalState(builder, "contentId"));
    }

    @Test
    public void setLocation() throws Exception {
        String location = "http://test.com/page_location";
        assertThat(builder, is(builder.setLocation(location)));
        assertEquals(location, Whitebox.getInternalState(builder, "location"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setLocationNotUrl() throws Exception {
        builder.setLocation("location");
    }

    @Test(expected = IllegalArgumentException.class)
    public void setLocationNull() throws Exception {
        builder.setLocation(null);
    }

    @Test
    public void setReferrer() throws Exception {
        String referrer = "http://test.com/referrer";
        assertThat(builder, is(builder.setReferrer(referrer)));
        assertEquals(referrer, Whitebox.getInternalState(builder, "referrer"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setReferrerNotUrl() throws Exception {
        builder.setReferrer("location");
    }

    @Test(expected = IllegalArgumentException.class)
    public void setReferrerNull() throws Exception {
        builder.setReferrer(null);
    }

    @Test
    public void setGoalId() throws Exception {
        String goalId = "goalId";
        assertThat(builder, is(builder.setGoalId(goalId)));
        assertEquals(goalId, Whitebox.getInternalState(builder, "goalId"));
    }

    @Test
    public void setPageName() throws Exception {
        String pageName = "pageName";
        assertThat(builder, is(builder.setPageName(pageName)));
        assertEquals(pageName, Whitebox.getInternalState(builder, "pageName"));
    }

    @Test
    public void setNewUser() throws Exception {
        assertThat(builder, is(builder.setNewUser(true)));
        assertTrue(Whitebox.getInternalState(builder, "isNewUser"));
    }

    @Test
    public void setUserLocation() throws Exception {
        Location userLocation = new Location("gps");
        assertThat(builder, is(builder.setUserLocation(userLocation)));
        assertThat(userLocation, is((Location) Whitebox.getInternalState(builder, "userLocation")));
    }

    @Test
    public void addCustomParameter() throws Exception {
        Map<String, String> map = Whitebox.getInternalState(builder, "customParameters");
        assertThat(builder, is(builder.addCustomParameter("key", "value")));
        assertThat(map, hasEntry("key", "value"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void addCustomParameterNameNull() throws Exception {
        builder.addCustomParameter(null, "any");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addCustomParameterValueNull() throws Exception {
        builder.addCustomParameter("any", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addCustomParameterLongValue() throws Exception {
        char[] chars = new char[PageViewEvent.MAX_CUSTOM_PARAMETER_LENGTH + 1];
        Arrays.fill(chars, ' ');
        builder.addCustomParameter("any", new String(chars));
    }

    @Test
    public void addCustomUserParameter() throws Exception {
        Map<String, String> map = Whitebox.getInternalState(builder, "customUserParameters");
        assertThat(builder, is(builder.addCustomUserParameter("key", "value")));
        assertThat(map, hasEntry("key", "value"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void addCustomUserParameterNameNull() throws Exception {
        builder.addCustomUserParameter(null, "any");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addCustomUserParameterValueNull() throws Exception {
        builder.addCustomUserParameter("any", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addCustomUserParameterLongValue() throws Exception {
        char[] chars = new char[PageViewEvent.MAX_CUSTOM_PARAMETER_LENGTH + 1];
        Arrays.fill(chars, ' ');
        builder.addCustomUserParameter("any", new String(chars));
    }

    @Test
    public void addExternalUserId() throws Exception {
        builder.addExternalUserId("xyz", "john.doe@example.com");
        assertThat(Whitebox.getInternalState(builder, "externalUserIds"), hasSize(1));
    }

    @Test
    public void addExternalUserIdMoreThanMaximum() throws Exception {
        for (int i = 0; i < PageViewEvent.MAX_EXTERNAL_USER_IDS * 2; i++) {
            builder.addExternalUserId("xyz", "john.doe@example.com");
        }
        assertThat(Whitebox.getInternalState(builder, "externalUserIds"), hasSize(PageViewEvent.MAX_EXTERNAL_USER_IDS));
    }

    @Test
    public void clearExternalUserIds() throws Exception {
        ArrayFixedSizeQueue<ExternalUserId> ids = Whitebox.getInternalState(builder, "externalUserIds");
        ids.add(new ExternalUserId("xyz", "john.doe@example.com"));
        builder.clearExternalUserIds();
        assertThat(ids, empty());
    }

    @Test
    public void build() throws Exception {
        Whitebox.setInternalState(builder, "location", "http://test.com/page_location");
        assertThat(event, is(builder.build()));
    }

    @Test
    public void buildUrlLess() throws Exception {
        Whitebox.setInternalState(builder, "contentId", "location");
        assertThat(event, is(builder.build()));
    }

    @Test(expected = IllegalStateException.class)
    public void buildFailed() throws Exception {
        builder.build();
    }
}