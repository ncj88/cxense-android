package com.cxense.cxensesdk;

import org.junit.Test;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mock;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-05).
 */
public class UserAgentInterceptorTest extends BaseInterceptorTest {
    private static final String USER_AGENT = "some custom test user agent";

    @Test
    public void intercept() throws Exception {
        mockWebServer.enqueue(new MockResponse());
        UserAgentProvider mockUserAgentProvider = mock(UserAgentProvider.class);
        doReturn(USER_AGENT).when(mockUserAgentProvider).getUserAgent();

        OkHttpClient okHttpClient = new OkHttpClient()
                .newBuilder()
                .addInterceptor(new UserAgentInterceptor(mockUserAgentProvider))
                .build();
        okHttpClient.newCall(new Request.Builder().url(mockWebServer.url("/")).build()).execute();

        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals(USER_AGENT, request.getHeader("User-Agent"));
    }

}