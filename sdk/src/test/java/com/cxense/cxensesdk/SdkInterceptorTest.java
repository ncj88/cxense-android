package com.cxense.cxensesdk;

import org.junit.Test;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-05).
 */
public class SdkInterceptorTest extends BaseInterceptorTest {
    @Test
    public void intercept() throws Exception {
        mockWebServer.enqueue(new MockResponse());

        OkHttpClient okHttpClient = new OkHttpClient()
                .newBuilder()
                .addInterceptor(new SdkInterceptor(BuildConfig.SDK_NAME, BuildConfig.VERSION_NAME))
                .build();
        okHttpClient.newCall(new Request.Builder().url(mockWebServer.url("/")).build()).execute();

        RecordedRequest request = mockWebServer.takeRequest();
        String path = request.getPath();
        assertThat(path, allOf(
                containsString("sdk=" + BuildConfig.SDK_NAME),
                containsString("sdkp=android"),
                containsString("sdkv=" + BuildConfig.VERSION_NAME)
        ));
    }

}