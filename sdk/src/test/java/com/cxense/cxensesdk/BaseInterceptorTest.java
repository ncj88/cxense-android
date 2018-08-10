package com.cxense.cxensesdk;

import org.junit.After;
import org.junit.Before;

import okhttp3.mockwebserver.MockWebServer;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-05).
 */
public abstract class BaseInterceptorTest {
    protected MockWebServer mockWebServer;

    @Before
    public void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @After
    public void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

}
