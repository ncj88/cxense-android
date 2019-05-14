package com.cxense.cxensesdk.model;

import com.cxense.cxensesdk.BaseTest;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ImpressionTest extends BaseTest {
    private Impression impression;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        impression = new Impression("url", 1);
    }

    @Test
    public void getClickUrl() {
        String url = "clickUrl";
        impression.clickUrl = url;
        assertEquals(url, impression.getClickUrl());
    }

    @Test
    public void getSeconds() {
        int seconds = 12345;
        impression.seconds = seconds;
        assertEquals(seconds, impression.getSeconds());
    }
}