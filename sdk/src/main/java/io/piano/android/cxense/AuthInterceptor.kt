package io.piano.android.cxense

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import retrofit2.Invocation
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * Interceptor for adding `X-cXense-Authentication` auth header.
 */
class AuthInterceptor(
    private val cxenseConfiguration: CxenseConfiguration,
) : Interceptor {
    internal val dateString: String
        get() = DATE_FORMAT.format(Date())

    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(chain.request().addAuth())
    }

    private fun Request.addAuth(): Request {
        try {
            return tag(Invocation::class.java)?.method()?.getAnnotation(Authorized::class.java)?.let {
                newBuilder()
                    .header(
                        AUTH_HEADER,
                        cxenseConfiguration.credentialsProvider.run {
                            createToken(getUsername(), getApiKey())
                        }
                    )
                    .build()
            } ?: this
        } catch (e: Exception) {
            throw BaseException("Failed to create authenticationToken!", e)
        }
    }

    internal fun createToken(username: String, secret: String): String {
        val date = dateString
        return Mac.getInstance(ALGORITHM)
            .apply {
                init(SecretKeySpec(secret.toByteArray(), ALGORITHM))
                update(date.toByteArray())
            }
            .doFinal()
            .joinToString(prefix = "username=$username date=$date hmac-sha256-hex=", separator = "") {
                "%02X".format(it)
            }
    }

    companion object {
        const val AUTH_HEADER = "X-cXense-Authentication"
        private const val ISO_8601 = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"

        @JvmStatic
        val DATE_FORMAT: DateFormat = SimpleDateFormat(ISO_8601, Locale.US)
        private const val ALGORITHM = "HmacSHA256"
    }
}
