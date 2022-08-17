package io.piano.android.cxense.model

import io.piano.android.cxense.assertFailsWithMessage
import kotlin.test.BeforeTest
import kotlin.test.Test

class ConversionEventBuilderTest {
    private lateinit var builder: ConversionEvent.Builder

    @BeforeTest
    fun setUp() {
        builder = ConversionEvent.Builder("siteId", "productId", ConversionEvent.FUNNEL_TYPE_CONVERT_PRODUCT)
    }

    @Test
    fun buildEmptySiteId() {
        assertFailsWithMessage<IllegalStateException>("Site id", "Expected fail for site id") {
            builder.siteId("").build()
        }
    }

    @Test
    fun buildEmptyProductId() {
        assertFailsWithMessage<IllegalStateException>("Product id can't be empty", "Expected fail for product id") {
            builder.productId("").build()
        }
    }

    @Test
    fun buildLongProductId() {
        assertFailsWithMessage<IllegalStateException>("Product id can't be longer", "Expected fail for product id") {
            builder.productId("1".repeat(ConversionEvent.MAX_LENGTH * 2)).build()
        }
    }

    @Test
    fun buildEmptyFunnelStep() {
        assertFailsWithMessage<IllegalStateException>("Funnel step can't be empty", "Expected fail for funnel step") {
            builder.funnelStep("").build()
        }
    }

    @Test
    fun buildLongFunnelStep() {
        assertFailsWithMessage<IllegalStateException>("Funnel step can't be longer", "Expected fail for funnel step") {
            builder.funnelStep("1".repeat(ConversionEvent.MAX_LENGTH * 2)).build()
        }
    }

    @Test
    fun buildInvalidRenewalFrequency() {
        assertFailsWithMessage<IllegalStateException>("renewal frequency", "Expected fail for renewal frequency") {
            builder.renewalFrequency("blablabla").build()
        }
    }
}
