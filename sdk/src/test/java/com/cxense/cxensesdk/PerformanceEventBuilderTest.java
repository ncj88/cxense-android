package com.cxense.cxensesdk;

import com.cxense.cxensesdk.model.CustomParameter;
import com.cxense.cxensesdk.model.UserIdentity;

import org.junit.Before;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.reflect.Whitebox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-07-31).
 */
@PrepareForTest({PerformanceEvent.class})
public class PerformanceEventBuilderTest extends BaseTest {
    private PerformanceEvent.Builder builder;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        PerformanceEvent event = mock(PerformanceEvent.class);
        builder = spy(new PerformanceEvent.Builder(new ArrayList<>(), "siteId", "xyz-origin", "type"));
        whenNew(PerformanceEvent.class).withAnyArguments().thenReturn(event);
    }

    @Test
    public void setTime() throws Exception {
        long time = 1000;
        builder.setTime(time);
        assertEquals(TimeUnit.MILLISECONDS.toSeconds(time), (long) Whitebox.getInternalState(builder, "time"));
    }

    @Test
    public void setTimeDate() throws Exception {
        Date date = new Date(1000);
        doNothing().when(builder).setTime(anyLong());
        assertThat(builder, is(builder.setTime(date)));
        verify(builder).setTime(anyLong());
    }

    @Test
    public void setCurrentTime() throws Exception {
        spy(System.class);
        doNothing().when(builder).setTime(anyLong());
        assertThat(builder, is(builder.setCurrentTime()));
        verify(builder).setTime(anyLong());
        verifyStatic();
        System.currentTimeMillis();
    }

    @Test
    public void addIdentity() throws Exception {
        List<UserIdentity> identities = Whitebox.getInternalState(builder, "identities");
        int size = identities.size();
        assertThat(builder, is(builder.addIdentity(new UserIdentity("id", "type"))));
        assertEquals(size + 1, identities.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void addIdentityNull() throws Exception {
        builder.addIdentity(null);
    }

    @Test
    public void addIdentities() throws Exception {
        List<UserIdentity> identities = Whitebox.getInternalState(builder, "identities");
        List<UserIdentity> newIdentities = Arrays.asList(new UserIdentity("id", "type"), new UserIdentity("id", "type"));
        int size = identities.size();
        assertThat(builder, is(builder.addIdentities(newIdentities)));
        assertEquals(size + newIdentities.size(), identities.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void addIdentitiesNull() throws Exception {
        builder.addIdentities(null);
    }

    @Test
    public void setPrnd() throws Exception {
        String prnd = "prnd";
        assertThat(builder, is(builder.setPrnd(prnd)));
        assertEquals(prnd, Whitebox.getInternalState(builder, "prnd"));
    }

    @Test
    public void setRnd() throws Exception {
        String rnd = "rnd";
        assertThat(builder, is(builder.setRnd(rnd)));
        assertEquals(rnd, Whitebox.getInternalState(builder, "rnd"));
    }

    @Test
    public void setSiteId() throws Exception {
        String siteId = "siteId";
        assertThat(builder, is(builder.setSiteId(siteId)));
        assertEquals(siteId, Whitebox.getInternalState(builder, "siteId"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setSiteIdNull() throws Exception {
        builder.setSiteId(null);
    }

    @Test
    public void setOrigin() throws Exception {
        String origin = "xyz-origin";
        assertThat(builder, is(builder.setOrigin(origin)));
        assertEquals(origin, Whitebox.getInternalState(builder, "origin"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setOriginNull() throws Exception {
        builder.setOrigin(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setOriginInvalid() throws Exception {
        builder.setOrigin("origin");
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
    public void addSegments() throws Exception {
        List<String> segments = Whitebox.getInternalState(builder, "segments");
        List<String> newSegments = Arrays.asList("1", "2");
        int size = segments.size();
        assertThat(builder, is(builder.addSegments(newSegments)));
        assertEquals(size + newSegments.size(), segments.size());
    }

    @Test
    public void addCustomParameter() throws Exception {
        List<CustomParameter> customParameters = Whitebox.getInternalState(builder, "customParameters");
        int size = customParameters.size();
        assertThat(builder, is(builder.addCustomParameter(new CustomParameter("name", "item"))));
        assertEquals(size + 1, customParameters.size());
    }

    @Test
    public void addCustomParameters() throws Exception {
        List<CustomParameter> customParameters = Whitebox.getInternalState(builder, "customParameters");
        List<CustomParameter> newCustomParameters = Arrays.asList(new CustomParameter("name", "item"), new CustomParameter("name2", "item2"));
        int size = customParameters.size();
        assertThat(builder, is(builder.addCustomParameters(newCustomParameters)));
        assertEquals(size + newCustomParameters.size(), customParameters.size());
    }

    @Test
    public void build() throws Exception {
        //TODO: fix test
        //assertThat(event, is(builder.build()));
    }

}