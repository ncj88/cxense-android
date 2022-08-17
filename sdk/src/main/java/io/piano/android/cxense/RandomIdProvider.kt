package io.piano.android.cxense

import java.util.Date

interface RandomIdProvider {
    fun getPageViewId(date: Date): String
}
