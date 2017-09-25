package com.cxense.cxensesdk.model;

import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasEntry;
import static org.junit.Assert.assertThat;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-07-26).
 */
public class WidgetItemTest {
    private WidgetItem widgetItem;

    @Before
    public void setUp() throws Exception {
        widgetItem = new WidgetItem();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getProperties() throws Exception {
        Map<String, Object> properties = widgetItem.getProperties();
        properties.put("test", "test");
    }

    @Test
    public void any() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        Whitebox.setInternalState(widgetItem, "properties", properties);
        assertThat(properties, is(widgetItem.any()));
    }

    @Test
    public void set() throws Exception {
        Map<String, Object> properties = Whitebox.getInternalState(widgetItem, "properties");
        widgetItem.set("key", "value");
        assertThat(properties, hasEntry("key", "value"));
    }

}