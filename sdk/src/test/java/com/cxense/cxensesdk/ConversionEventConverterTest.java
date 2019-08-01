package com.cxense.cxensesdk;

import com.cxense.cxensesdk.model.ConversionEvent;
import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;

public class ConversionEventConverterTest extends BaseTest {
    private Gson gson;
    private ConversionEventConverter converter;
    private ConversionEvent event;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        gson = mock(Gson.class);
        converter = new ConversionEventConverter(gson);
        event = mock(ConversionEvent.class);
    }

    @Test
    public void canConvert() {
        assertTrue(converter.canConvert(event));
    }

    @Test
    public void toQueryMap() {
        assertNull(converter.toQueryMap(event));
    }

    @Test
    public void toEventRecord() throws Exception {
        assertNotNull(converter.toEventRecord(event));
        verify(gson).toJson(event);
    }
}