package com.example.cxensesdk

import android.util.Log
import timber.log.Timber

class LoggingTree : Timber.DebugTree() {
    override fun isLoggable(tag: String?, priority: Int): Boolean = BuildConfig.DEBUG || priority >= Log.ERROR
}