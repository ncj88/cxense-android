package com.cxense.cxensesdk;

import org.junit.Before;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.util.ArrayList;

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
    public void toEventRecord() throws Exception {
        assertNotNull(event.toEventRecord());
        verify(cxense).packObject(any());
    }

}