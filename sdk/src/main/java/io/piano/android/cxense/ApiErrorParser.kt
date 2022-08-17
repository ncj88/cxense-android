package io.piano.android.cxense

import io.piano.android.cxense.model.ApiError
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Response
import timber.log.Timber
import java.io.IOException

/**
 * Error parser
 */
class ApiErrorParser(
    private val converter: Converter<ResponseBody, ApiError>
) {
    fun parseError(response: Response<*>): BaseException? {
        if (response.isSuccessful)
            return null
        try {
            val apiError: ApiError = response.errorBody()?.let {
                try {
                    converter.convert(it)
                } catch (e: IOException) {
                    null
                }
            } ?: ApiError()
            val message = apiError.error ?: ""
            return when (response.code()) {
                400 -> BadRequestException(message)
                401 -> NotAuthorizedException(message)
                403 -> ForbiddenException(message)
                else -> BaseException(message)
            }
        } catch (e: Exception) {
            Timber.e(e)
            return BaseException(e)
        }
    }
}
