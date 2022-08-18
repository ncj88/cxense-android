package com.example.cxensesdk

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import by.kirich1409.viewbindingdelegate.viewBinding
import io.piano.android.cxense.CxenseSdk
import io.piano.android.cxense.LoadCallback
import io.piano.android.cxense.model.ConversionEvent
import io.piano.android.cxense.model.CustomParameter
import io.piano.android.cxense.model.EventStatus
import io.piano.android.cxense.model.Impression
import io.piano.android.cxense.model.PageViewEvent
import io.piano.android.cxense.model.PerformanceEvent
import io.piano.android.cxense.model.UserIdentity
import io.piano.android.cxense.model.WidgetContext
import io.piano.android.cxense.model.WidgetItem
import com.example.cxensesdk.databinding.ActivityAnimalBinding
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber

class AnimalActivity : AppCompatActivity(R.layout.activity_animal) {
    private val binding: ActivityAnimalBinding by viewBinding(R.id.animalText)
    private lateinit var item: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        item = intent.getStringExtra(ITEM_KEY) ?: ""
        binding.animalText.text = getString(R.string.item_text, item)
    }

    override fun onPause() {
        CxenseSdk.getInstance().trackActiveTime(item)
        CxenseSdk.getInstance().setDispatchEventsCallback(null)
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        CxenseSdk.getInstance().setDispatchEventsCallback { statuses ->
            val grouped = statuses.groupBy { it.isSent }
            val message =
                """
                        Sent: '${grouped[true]?.joinToString { it.eventId ?: "" }}'
                        Not sent: '${grouped[false]?.joinToString { it.eventId ?: "" }}'
                        """.trimIndent()
            runOnUiThread {
                Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
            }
        }
        CxenseSdk.getInstance().pushEvents(
            PageViewEvent.Builder(BuildConfig.SITE_ID)
                .contentId(item)
                .eventId(item)
                .addCustomParameters(CustomParameter("cxd-item", item))
                .build(),
            PerformanceEvent.Builder(BuildConfig.SITE_ID, "cxd-app", "view")
                .addIdentities(UserIdentity("cxd", "value"))
                .eventId(item)
                .addCustomParameters(CustomParameter("xyz-item", item))
                .build(),
            ConversionEvent.Builder(
                BuildConfig.SITE_ID,
                "0ab24abee9a85d869b29f46c837144",
                ConversionEvent.FUNNEL_TYPE_CONVERT_PRODUCT,
                mutableListOf(UserIdentity("cxd", "123456"))
            )
                .productPrice(12.25)
                .renewalFrequency("1wC")
                .build()
        )
        CxenseSdk.getInstance().loadWidgetRecommendations(
            "ffb1d2523b582f5f649df351d37928d2c108e715",
            WidgetContext.Builder("https://cxense.com").build(),
            callback = object : LoadCallback<List<WidgetItem>> {
                override fun onSuccess(data: List<WidgetItem>) {
                    CxenseSdk.getInstance().reportWidgetVisibilities(
                        object : LoadCallback<Any> {
                            override fun onSuccess(data: Any) {
                                Timber.d("Success")
                            }

                            override fun onError(throwable: Throwable) {
                                Timber.e(throwable)
                            }
                        },
                        Impression(data[0].clickUrl ?: "", 1),
                        Impression(data[1].clickUrl ?: "", 2)
                    )
                }

                override fun onError(throwable: Throwable) {
                    Timber.e(throwable)
                }
            }
        )
    }

    companion object {
        const val ITEM_KEY = "item"
    }
}
