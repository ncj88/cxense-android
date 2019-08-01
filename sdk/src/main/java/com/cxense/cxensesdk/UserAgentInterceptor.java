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
    private final UserAgentProvider userAgentProvider;

    /**
     * @param userAgentProvider user-agent string
     */
    UserAgentInterceptor(@NonNull UserAgentProvider userAgentProvider) {
        this.userAgentProvider = userAgentProvider;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request newRequest = request.newBuilder()
                .header("User-Agent", userAgentProvider.getUserAgent())
                .build();
        return chain.proceed(newRequest);
    }
}
