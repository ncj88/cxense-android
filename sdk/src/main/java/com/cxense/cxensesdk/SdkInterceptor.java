package com.cxense.cxensesdk;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Interceptor for setting params for analytic.
 *
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-05).
 */
public class SdkInterceptor implements Interceptor {
    private final String sdkName;
    private final String sdkVersion;

    public SdkInterceptor(String sdkName, String sdkVersion) {
        this.sdkName = sdkName;
        this.sdkVersion = sdkVersion;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        HttpUrl url = request.url().newBuilder()
                .addQueryParameter("sdk", sdkName)
                .addQueryParameter("sdkp", "android")
                .addQueryParameter("sdkv", sdkVersion)
                .build();
        Request newRequest = request.newBuilder()
                .url(url)
                .build();
        return chain.proceed(newRequest);
    }
}
