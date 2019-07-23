package com.cxense.cxensesdk.model;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-07-26).
 */
public class WidgetItemTest {
    private WidgetItem widgetItem;

    @Before
    public void setUp() throws Exception {
        widgetItem = new WidgetItem("", "", "", new HashMap<>());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getProperties() throws Exception {
        widgetItem.properties.put("test", "test");
    }

}