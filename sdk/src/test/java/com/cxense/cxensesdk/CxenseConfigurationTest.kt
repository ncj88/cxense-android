package com.cxense.cxensesdk

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import java.util.concurrent.TimeUnit
import kotlin.test.BeforeTest
import kotlin.test.Test

class CxenseConfigurationTest {
    private val dispatchPeriodListener: ((Long) -> Unit) = mock()

    private lateinit var configuration: CxenseConfiguration

    @BeforeTest
    fun setUp() {
        configuration = CxenseConfiguration()
        configuration.dispatchPeriodListener = dispatchPeriodListener
    }

    @Test
    fun setDifferentDispatchPeriod() {
        configuration.dispatchPeriod(configuration.dispatchPeriod * 2, TimeUnit.MILLISECONDS)
        verify(dispatchPeriodListener).invoke(any())
    }

    @Test
    fun setSameDispatchPeriod() {
        configuration.dispatchPeriod(configuration.dispatchPeriod, TimeUnit.MILLISECONDS)
        verify(dispatchPeriodListener, never()).invoke(any())
    }

    @Test
    fun setInvalidDispatchPeriod() {
        assertFailsWithMessage<IllegalArgumentException>(
            "greater than $MIN_DISPATCH_PERIOD_SECONDS seconds",
            "Expected fail for dispatch period"
        ) {
            configuration.dispatchPeriod(MIN_DISPATCH_PERIOD - 1, TimeUnit.MILLISECONDS)
        }
        verify(dispatchPeriodListener, never()).invoke(any())
    }

    @Test
    fun setInvalidOutdatePeriod() {
        assertFailsWithMessage<IllegalArgumentException>(
            "greater than $MIN_OUTDATE_PERIOD_SECONDS seconds",
            "Expected fail for dispatch period"
        ) {
            configuration.outdatePeriod(MIN_OUTDATE_PERIOD - 1, TimeUnit.MILLISECONDS)
        }
    }
}
