package com.cxense.cxensesdk

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ApiCallback<T>(
    private val callback: LoadCallback<T>,
    private val errorParser: ApiErrorParser
) : Callback<T> {
    override fun onFailure(call: Call<T>, t: Throwable) {
        callback.onError(t)
    }

    override fun onResponse(call: Call<T>, response: Response<T>) {
        val body = response.body()
        if (response.isSuccessful && body != null)
            callback.onSuccess(body)
        else {
            val e = errorParser.parseError(response) ?: BaseException("Response body is null")
            callback.onError(e)
        }
    }
}