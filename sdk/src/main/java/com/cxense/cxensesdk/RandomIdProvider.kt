package com.cxense.cxensesdk

import java.util.Date

interface RandomIdProvider {
    fun getPageViewId(date: Date): String
}
