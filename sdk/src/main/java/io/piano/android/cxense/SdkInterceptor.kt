package io.piano.android.cxense

import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor for setting params for analytic.
 *
 */
internal class SdkInterceptor(
    private val sdkName: String,
    private val sdkVersion: String,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response =
        chain.proceed(
            chain.request()
                .run {
                    newBuilder()
                        .url(url.addSdkParams())
                        .build()
                }
        )

    private fun HttpUrl.addSdkParams() =
        newBuilder()
            .addQueryParameter(ARG_SDK_NAME, sdkName)
            .addQueryParameter(ARG_SDK_PLATFORM, VALUE_SDK_PLATFORM)
            .addQueryParameter(ARG_SDK_VERSION, sdkVersion)
            .build()

    companion object {
        const val ARG_SDK_NAME = "sdk"
        const val ARG_SDK_PLATFORM = "sdkp"
        const val ARG_SDK_VERSION = "sdkv"
        const val VALUE_SDK_PLATFORM = "android"
    }
}
