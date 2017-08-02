package com.cxense.cxensesdk.model;

import com.cxense.cxensesdk.BaseTest;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-07-31).
 */
public class ExternalItemTest extends BaseTest {
    private ExternalItem externalItem;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        externalItem = new ExternalItem("group", "item");
    }

    @Test
    public void getGroup() throws Exception {
        String group = "group";
        externalItem.group = group;
        assertEquals(group, externalItem.getGroup());
    }

    @Test
    public void getItem() throws Exception {
        String item = "item";
        externalItem.group = item;
        assertEquals(item, externalItem.getItem());
    }
}