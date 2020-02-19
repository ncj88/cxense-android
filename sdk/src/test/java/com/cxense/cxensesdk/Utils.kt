package com.cxense.cxensesdk

import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

inline fun <reified T : Throwable> assertFailsWithMessage(
    expectedSubstring: String,
    failMessage: String? = null,
    crossinline block: () -> Unit
) = assertTrue(failMessage) {
    assertFailsWith<T>(block = block).message?.contains(expectedSubstring) ?: false
}
