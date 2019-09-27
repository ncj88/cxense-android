package com.example.cxensesdk

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.cxense.cxensesdk.CxenseSdk
import com.cxense.cxensesdk.LoadCallback
import com.cxense.cxensesdk.model.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_animal.*

class AnimalActivity : AppCompatActivity() {
    private lateinit var item: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_animal)
        item = intent.getStringExtra(ITEM_KEY) ?: ""
        animalText.text = getString(R.string.item_text, item)
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
            val message = "Sent: '${grouped[true]?.joinToString { it.eventId ?: "" }}'\nNot sent: '${grouped[false]?.joinToString { it.eventId ?: "" }}'"
            Snackbar.make(animalText, message, Snackbar.LENGTH_LONG).show()
        }
        CxenseSdk.getInstance().pushEvents(
                PageViewEvent.Builder(BuildConfig.SITE_ID)
                        .setContentId(item)
                        .setEventId(item)
                        .addCustomParameter("xyz-item", item)
                        .build(),
                PerformanceEvent.Builder(listOf(), BuildConfig.SITE_ID, "xyz-app", "view")
                        .setEventId(item)
                        .addCustomParameters(CustomParameter("xyz-item", item))
                        .build(),
                ConversionEvent.Builder(listOf(UserIdentity("123456", "cxd")), BuildConfig.SITE_ID, "0ab24abee9a85d869b29f46c837144", ConversionEvent.FUNNEL_TYPE_CONVERT_PRODUCT)
                        .setPrice(12.25)
                        .setRenewalFrequency("1wC")
                        .build()
        )
        CxenseSdk.getInstance().loadWidgetRecommendations("ffb1d2523b582f5f649df351d37928d2c108e715", WidgetContext.Builder("https://cxense.com").build(), object : LoadCallback<List<WidgetItem>> {
            override fun onSuccess(data: List<WidgetItem>) {
                CxenseSdk.getInstance().reportWidgetVisibilities(
                        object : LoadCallback<Any> {
                            override fun onSuccess(data: Any) {
                                Log.d("WVR", "Success")
                            }

                            override fun onError(throwable: Throwable) {
                                Log.e("WVR", throwable.message, throwable)
                            }
                        },
                        Impression(data[0].clickUrl, 1),
                        Impression(data[1].clickUrl, 2)
                )
            }

            override fun onError(throwable: Throwable) {
                Log.e("WVR", throwable.message, throwable)
            }
        })

    }

    companion object {
        const val ITEM_KEY = "item"
    }
}
