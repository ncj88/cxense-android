package io.piano.android.cxense

import okhttp3.ResponseBody.Companion.toResponseBody
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import retrofit2.Response
import kotlin.test.Test

class ApiCallbackTest {
    private val callback: LoadCallback<Any> = mock()
    private val apiErrorParser: ApiErrorParser = mock()
    private val apiCallback = ApiCallback(callback, apiErrorParser)

    @Test
    fun onFailure() {
        val e = Throwable()
        apiCallback.onFailure(mock(), e)
        verify(callback).onError(eq(e))
    }

    @Test
    fun onResponseSuccessful() {
        val body: Any = mock()
        val response = Response.success(body)
        apiCallback.onResponse(mock(), response)
        verify(callback).onSuccess(eq(body))
    }

    @Test
    fun onResponseSuccessfulWithoutBody() {
        val response: Response<Any> = Response.success(null)
        checkFailedResponse(response, BaseException())
    }

    @Test
    fun onResponseSuccessfulWithoutBodyErrorNotParsed() {
        val response: Response<Any> = Response.success(null)
        checkFailedResponse(response, null)
    }

    @Test
    fun onResponseFailed() {
        val response = Response.error<Any>(400, "".toResponseBody())
        checkFailedResponse(response, BaseException())
    }

    @Test
    fun onResponseFailedErrorNotParsed() {
        val response = Response.error<Any>(400, "".toResponseBody())
        checkFailedResponse(response, null)
    }

    private fun checkFailedResponse(response: Response<Any>, exception: BaseException?) {
        whenever(apiErrorParser.parseError(response)).thenReturn(exception)
        apiCallback.onResponse(mock(), response)
        verify(apiErrorParser).parseError(eq(response))
        verify(callback).onError(any())
    }
}
