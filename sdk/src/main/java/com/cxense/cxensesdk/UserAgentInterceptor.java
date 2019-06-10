package com.cxense.cxensesdk;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Interceptor for setting User-Agent header.
 *
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-05).
 */
class UserAgentInterceptor implements Interceptor {
    private final String userAgent;

    /**
     * @param userAgent user-agent string
     */
    UserAgentInterceptor(@NonNull String userAgent) {
        this.userAgent = userAgent;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request newRequest = request.newBuilder()
                .header("User-Agent", userAgent)
                .build();
        return chain.proceed(newRequest);
    }
}
