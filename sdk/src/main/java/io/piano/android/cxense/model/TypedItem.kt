package io.piano.android.cxense.model

import com.squareup.moshi.JsonClass
import io.piano.android.cxense.DoubleString
import io.piano.android.cxense.IntString
import java.util.Date

sealed class TypedItem {
    @JsonClass(generateAdapter = true)
    class String(val value: kotlin.String) : TypedItem()

    @JsonClass(generateAdapter = true)
    class Number(@IntString val value: Int) : TypedItem() {
        init {
            require(value >= 0)
        }
    }

    @JsonClass(generateAdapter = true)
    class Time(val value: Date) : TypedItem()

    @JsonClass(generateAdapter = true)
    class Decimal(@DoubleString val value: Double) : TypedItem() {
        init {
            require(value >= 0 && value * 100 <= Int.MAX_VALUE)
        }
    }

    object Unknown : TypedItem()
}
