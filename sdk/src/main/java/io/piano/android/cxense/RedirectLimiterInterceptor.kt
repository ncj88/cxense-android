package io.piano.android.cxense

import okhttp3.Interceptor
import okhttp3.Response

class RedirectLimiterInterceptor(
    urlBlockPattern: String,
) : Interceptor {
    private val urlBlockRegex = urlBlockPattern.toRegex()
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        return if (response.request.url.toString().contains(urlBlockRegex)) {
            response.newBuilder()
                .removeHeader("Location")
                .code(200)
                .message("OK")
                .build()
        } else {
            response
        }
    }
}
