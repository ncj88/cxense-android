package com.cxense.cxensesdk;

import com.cxense.cxensesdk.model.CustomParameter;
import com.cxense.cxensesdk.model.CxenseUserIdentity;
import com.cxense.cxensesdk.model.PerformanceEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasKey;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2018-10-19).
 */
@PrepareForTest({CxenseConfiguration.class, PerformanceEvent.class})
public class PerformanceEventConverterTest extends BaseTest {
    private ObjectMapper mapper;
    private CxenseConfiguration configuration;
    private PerformanceEventConverter converter;
    private PerformanceEvent event;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        mapper = mock(ObjectMapper.class);
        configuration = mock(CxenseConfiguration.class);
        converter = new PerformanceEventConverter(mapper, configuration);
        event = mock(PerformanceEvent.class);
        when(event.getTime()).thenReturn(new Date(0));
    }

    @Test
    public void canConvert() {
        assertTrue(converter.canConvert(event));
    }

    @Test
    public void toQueryMap() {
        final CustomParameter customParameter = new CustomParameter("name", "item");
        final CxenseUserIdentity userIdentity = new CxenseUserIdentity("type", "id");
        when(event.getCustomParameters()).thenReturn(Collections.singletonList(customParameter));
        when(event.getIdentities()).thenReturn(Collections.singletonList(userIdentity));
        when(event.getSegments()).thenReturn(Collections.singletonList("segment"));
        when(configuration.getConsentOptionsAsString()).thenReturn(ConsentOption.CONSENT_REQUIRED.getValue());
        Map<String, String> result = converter.toQueryMap(event);
        assertThat(result, allOf(
                hasKey(PerformanceEvent.TIME),
                hasKey(PerformanceEvent.PRND),
                hasKey(PerformanceEvent.RND),
                hasKey(PerformanceEvent.SITE_ID),
                hasKey(PerformanceEvent.ORIGIN),
                hasKey(PerformanceEvent.TYPE),
                hasKey(PerformanceEvent.SEGMENT_IDS),
                hasKey(PerformanceEventConverter.CONSENT)
        ));
    }

    @Test
    public void toEventRecord() throws Exception {
        assertNotNull(converter.toEventRecord(event));
        verify(mapper).writeValueAsString(any());
    }
}