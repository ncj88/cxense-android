package io.piano.android.cxense

import android.app.Application
import android.content.Context
import android.os.Build
import android.webkit.WebSettings
import android.webkit.WebView
import timber.log.Timber

/**
 * Provides User-Agent
 *
 */
internal class UserAgentProvider(
    sdkVersion: String,
    context: Context,
) {
    val userAgent: String by lazy { "cx-sdk/$sdkVersion ${context.getDefaultUserAgent()}" }

    private fun Context.getDefaultUserAgent(): String =
        runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && packageName != Application.getProcessName()) {
                WebView.setDataDirectorySuffix(WEBVIEW_SUFFIX)
            }
            WebSettings.getDefaultUserAgent(this)
        }.recover {
            /*
            This block is needed as attempt to avoid problem with Android System WebView
            service's update during which any requests to WebViews will be finished
            with android.content.pm.PackageManager$NameNotFoundException.

            What is important here, that 'user-agent' is required param in Cxense Insight API,
            so, we need to provide it. We can use 'http.agent' property's value here, but
            it provides less details about device than WebView. That is why property's value
            is used without defaultUserAgent field's initialization.

            Best practise here - always using WebView's 'user-agent' string.

            Bug in Android issue tracker can be found here:
            https://code.google.com/p/android/issues/detail?id=175124

            Good explanation of the problem can be found here:
            https://bugs.chromium.org/p/chromium/issues/detail?id=506369
             */
            Timber.e(it)
            System.getProperty("http.agent") ?: ""
        }.map {
            it.filter { c -> c == '\t' || c in '\u0020'..'\u007e' }
        }.getOrDefault("")

    companion object {
        private const val WEBVIEW_SUFFIX = "CxenseSDK"
    }
}
