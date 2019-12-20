package com.cxense.cxensesdk

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import retrofit2.Response
import kotlin.test.Test

class ApiCallbackTest {
    private val response: Response<Any> = mock()
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
        whenever(response.isSuccessful).thenReturn(true)
        whenever(response.body()).thenReturn(body)
        apiCallback.onResponse(mock(), response)
        verify(response).body()
        verify(callback).onSuccess(eq(body))
    }

    @Test
    fun onResponseSuccessfulWithoutBody() {
        whenever(response.isSuccessful).thenReturn(true)
        whenever(response.body()).thenReturn(null)
        checkFailedResponse(response, BaseException())
    }

    @Test
    fun onResponseSuccessfulWithoutBodyErrorNotParsed() {
        whenever(response.isSuccessful).thenReturn(true)
        whenever(response.body()).thenReturn(null)
        checkFailedResponse(response, null)
    }

    @Test
    fun onResponseFailed() {
        whenever(response.isSuccessful).thenReturn(false)
        checkFailedResponse(response, BaseException())
    }

    @Test
    fun onResponseFailedErrorNotParsed() {
        whenever(response.isSuccessful).thenReturn(false)
        checkFailedResponse(response, null)
    }

    private fun checkFailedResponse(response: Response<Any>, exception: BaseException?) {
        whenever(apiErrorParser.parseError(response)).thenReturn(exception)
        apiCallback.onResponse(mock(), response)
        verify(response).body()
        verify(apiErrorParser).parseError(eq(response))
        verify(callback).onError(any())
    }
}
