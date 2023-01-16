package io.piano.android.cxense

import io.piano.android.cxense.model.ApiError
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Response
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ApiErrorParserTest {
    private val stubError = ApiError("Error")
    private val response: Response<Any> = mock {
        on { isSuccessful } doReturn false
        on { errorBody() } doReturn mock()
    }
    private val converter: Converter<ResponseBody, ApiError> = mock {
        on { convert(any()) } doReturn stubError
    }
    private val apiErrorParser = ApiErrorParser(converter)

    @Test
    fun parseErrorSuccessfulResponse() {
        whenever(response.isSuccessful).thenReturn(true)
        assertNull(apiErrorParser.parseError(response))
    }

    @Test
    fun parseErrorNoErrorBody() {
        whenever(response.errorBody()).thenReturn(null)
        val exc = apiErrorParser.parseError(response)
        assertNotNull(exc)
        assertEquals("", exc.message)
        verify(converter, never()).convert(any())
    }

    @Test
    fun parseErrorThrowException() {
        whenever(response.errorBody()).thenThrow(RuntimeException())
        val exc = apiErrorParser.parseError(response)
        assertNotNull(exc)
        assertNotNull(exc.cause)
        assertTrue { exc.cause is RuntimeException }
    }

    @Test
    fun parseError400() {
        checkResponseForCode<BadRequestException>(400)
    }

    @Test
    fun parseError401() {
        checkResponseForCode<NotAuthorizedException>(401)
    }

    @Test
    fun parseError403() {
        checkResponseForCode<ForbiddenException>(403)
    }

    private inline fun <reified T> checkResponseForCode(code: Int) {
        whenever(response.code()).thenReturn(code)
        val exc = apiErrorParser.parseError(response)
        verify(converter).convert(any())
        assertNotNull(exc)
        assertTrue { exc is T }
        assertEquals(stubError.error, exc.message)
    }
}
