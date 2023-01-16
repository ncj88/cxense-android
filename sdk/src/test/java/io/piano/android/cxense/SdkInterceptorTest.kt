package io.piano.android.cxense

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class SdkInterceptorTest : BaseInterceptorTest() {

    @Test
    fun intercept() {
        mockWebServer.enqueue(MockResponse())
        OkHttpClient.Builder()
            .addInterceptor(SdkInterceptor(SDK_NAME, SDK_VERSION_NAME))
            .build()
            .newCall(
                Request.Builder()
                    .url(mockWebServer.url("/"))
                    .build()
            )
            .execute()
        with(mockWebServer.takeRequest().requestUrl) {
            assertNotNull(this)
            assertEquals(SDK_NAME, queryParameter(SdkInterceptor.ARG_SDK_NAME))
            assertEquals(SDK_VERSION_NAME, queryParameter(SdkInterceptor.ARG_SDK_VERSION))
            assertEquals(SdkInterceptor.VALUE_SDK_PLATFORM, queryParameter(SdkInterceptor.ARG_SDK_PLATFORM))
        }
    }

    companion object {
        private const val SDK_NAME = "name"
        private const val SDK_VERSION_NAME = "version"
    }
}
