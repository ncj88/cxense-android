package com.cxense.cxensesdk;

import com.cxense.cxensesdk.model.CustomParameter;
import com.cxense.cxensesdk.model.UserIdentity;

import org.junit.Before;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.reflect.Whitebox;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-07-19).
 */
@PrepareForTest({PerformanceEvent.class})
public class PerformanceEventTest extends BaseTest {
    private PerformanceEvent event;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        event = spy(new PerformanceEvent.Builder(new ArrayList<>(), "siteId", "xyz-origin", "type").build());
    }

    @Test
    public void getTime() throws Exception {
        Date date = new Date(1000);
        Whitebox.setInternalState(event, "time", TimeUnit.MILLISECONDS.toSeconds(date.getTime()));
        assertThat(date, equalTo(event.getTime()));
    }

    @Test
    public void getIdentities() throws Exception {
        List<UserIdentity> identities = new ArrayList<>();
        Whitebox.setInternalState(event, "identities", identities);
        assertThat(identities, is(event.getIdentities()));
    }

    @Test
    public void getPrnd() throws Exception {
        String prnd = "prnd";
        Whitebox.setInternalState(event, "prnd", prnd);
        assertEquals(prnd, event.getPrnd());
    }

    @Test
    public void getRnd() throws Exception {
        String rnd = "rnd";
        Whitebox.setInternalState(event, "rnd", rnd);
        assertEquals(rnd, event.getRnd());
    }

    @Test
    public void getSiteId() throws Exception {
        String siteId = "siteId";
        Whitebox.setInternalState(event, "siteId", siteId);
        assertEquals(siteId, event.getSiteId());
    }

    @Test
    public void getOrigin() throws Exception {
        String origin = "origin";
        Whitebox.setInternalState(event, "origin", origin);
        assertEquals(origin, event.getOrigin());
    }

    @Test
    public void getType() throws Exception {
        String type = "type";
        Whitebox.setInternalState(event, "type", type);
        assertEquals(type, event.getType());
    }

    @Test
    public void getSegments() throws Exception {
        List<String> segments = new ArrayList<>();
        Whitebox.setInternalState(event, "segments", segments);
        assertThat(segments, is(event.getSegments()));
    }

    @Test
    public void getCustomParameters() throws Exception {
        List<CustomParameter> customParameters = new ArrayList<>();
        Whitebox.setInternalState(event, "customParameters", customParameters);
        assertThat(customParameters, is(event.getCustomParameters()));
    }

    @Test
    public void toEventRecord() throws Exception {
        assertNotNull(event.toEventRecord());
        verify(cxense).packObject(any());
    }

}