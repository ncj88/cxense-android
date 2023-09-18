package io.piano.android.cxense

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor for setting User-Agent header.
 *
 */
internal class UserAgentInterceptor(
    private val userAgentProvider: UserAgentProvider,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response =
        chain.proceed(
            chain.request()
                .newBuilder()
                .header("User-Agent", userAgentProvider.userAgent)
                .build()
        )
}
