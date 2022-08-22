package io.piano.android.cxense

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.whenever
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import retrofit2.Invocation
import java.lang.reflect.Method
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class AuthInterceptorTest : BaseInterceptorTest() {
    private lateinit var authInterceptor: AuthInterceptor

    private val credentialsProvider: CredentialsProvider = mock {
        on { getApiKey() } doReturn ""
        on { getUsername() } doReturn ""
    }
    private val cxenseConfiguration: CxenseConfiguration = mock {
        on { credentialsProvider } doReturn credentialsProvider
    }
    private val method: Method = mock {
        on { getAnnotation(eq(Authorized::class.java)) } doReturn mock()
    }
    private val invocation: Invocation = Invocation.of(method, listOf<Any>())

    @BeforeTest
    override fun setUp() {
        super.setUp()
        authInterceptor = spy(AuthInterceptor(cxenseConfiguration))
    }

    @Test
    fun intercept() {
        doReturn(AUTH_VALUE).`when`(authInterceptor).createToken(any(), any())
        mockWebServer.enqueue(MockResponse())
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()
            .newCall(
                Request.Builder()
                    .url(mockWebServer.url("/"))
                    .tag(Invocation::class.java, invocation)
                    .build()
            )
            .execute()
        assertEquals(AUTH_VALUE, mockWebServer.takeRequest().getHeader(AuthInterceptor.AUTH_HEADER))
    }

    @Test
    fun createToken() {
        whenever(authInterceptor.dateString).thenReturn(DATE_VALUE)
        assertEquals(AUTH_VALUE, authInterceptor.createToken(USER_VALUE, SECRET_VALUE))
    }

    companion object {
        private const val USER_VALUE = "testUser"
        private const val SECRET_VALUE = "secret"
        private const val DATE_VALUE = "1970-01-01T04:00:00.001+0400"
        private const val HASH_VALUE = "C266EF8D0CF01BE4ADA9DC6E4D4DBB9870DE3BD3A20BB3E060326D691597382A"
        private const val AUTH_VALUE = "username=$USER_VALUE date=$DATE_VALUE hmac-sha256-hex=$HASH_VALUE"
    }
}
