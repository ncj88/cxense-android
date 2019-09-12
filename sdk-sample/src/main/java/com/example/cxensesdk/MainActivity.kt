package com.example.cxensesdk

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.cxense.cxensesdk.CredentialsProvider
import com.cxense.cxensesdk.CxenseConstants
import com.cxense.cxensesdk.CxenseSdk
import com.cxense.cxensesdk.LoadCallback
import com.cxense.cxensesdk.model.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private val animals = listOf("alligator", "ant", "bear", "bee", "bird", "camel", "cat",
            "cheetah", "chicken", "chimpanzee", "cow", "crocodile", "deer", "dog", "dolphin", "duck",
            "eagle", "elephant", "fish", "fly", "fox", "frog", "giraffe", "goat", "goldfish", "hamster",
            "hippopotamus", "horse", "kangaroo", "kitten", "lion", "lobster", "monkey", "octopus", "owl",
            "panda", "pig", "puppy", "rabbit", "rat", "scorpion", "seal", "shark", "sheep", "snail",
            "snake", "spider", "squirrel", "tiger", "turtle", "wolf", "zebra"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.adapter = MainAdapter(animals, this::onItemClick)

        CxenseSdk.getInstance().configuration.apply {
            setDispatchPeriod(CxenseConstants.MIN_DISPATCH_PERIOD, TimeUnit.MILLISECONDS)
            credentialsProvider = object : CredentialsProvider {
                override fun getUsername(): String = BuildConfig.USERNAME // load it from secured store

                override fun getApiKey(): String = BuildConfig.API_KEY // load it from secured store

                override fun getDmpPushPersistentId(): String = BuildConfig.PERSISTED_ID
            }
        }
    }

    override fun onPause() {
        CxenseSdk.getInstance().setDispatchEventsCallback(null)
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        CxenseSdk.getInstance().setDispatchEventsCallback { statuses ->
            val grouped = statuses.groupBy { it.isSent }
            showText("Sent: '${grouped[true]?.joinToString { it.eventId ?: "" }}'\nNot sent: '${grouped[false]?.joinToString { it.eventId ?: "" }}'")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.run -> {
                runMethods()
                true
            }
            R.id.flush -> {
                CxenseSdk.getInstance().flushEventQueue()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun onItemClick(item: String) {
        Intent(this, AnimalActivity::class.java)
                .putExtra(AnimalActivity.ITEM_KEY, item)
                .also { startActivity(it) }
    }

    private fun showText(str: String) {
        Snackbar.make(recyclerview, str, Snackbar.LENGTH_LONG).show()
    }

    private fun showError(t: Throwable) {
        Log.e(TAG, t.message, t)
        showText(t.message ?: "")
    }

    private fun runMethods() {
        val id = "some_user_id"
        val type = "cxd"
        val segmentsPersistentId = "some_persistemt_id"
        val cxenseSdk = CxenseSdk.getInstance()
        val identity = UserIdentity(id, type)
        val identities = listOf(UserIdentity(id, type))
        cxenseSdk.loadWidgetRecommendations("w", WidgetContext.Builder("").build(), object : LoadCallback<List<WidgetItem>> {
            override fun onSuccess(data: List<WidgetItem>) {
                showText("TEXT")
            }

            override fun onError(throwable: Throwable) {
                showError(throwable)
            }
        })
        cxenseSdk.executePersistedQuery(CxenseConstants.ENDPOINT_USER_SEGMENTS, segmentsPersistentId, UserSegmentRequest(listOf(UserIdentity(id, type)), null), object : LoadCallback<SegmentsResponse> {
            override fun onSuccess(segmentsResponse: SegmentsResponse) {
                showText(TextUtils.join(" ", segmentsResponse.ids))
            }

            override fun onError(throwable: Throwable) {
                showError(throwable)
            }
        })
        cxenseSdk.getUserSegmentIds(identities, listOf(BuildConfig.SITE_ID), object : LoadCallback<List<String>> {
            override fun onSuccess(data: List<String>) {
                showText(TextUtils.join(" ", data))
            }

            override fun onError(t: Throwable) {
                showError(t)
            }
        })
        cxenseSdk.getUser(identity, object : LoadCallback<User> {
            override fun onSuccess(data: User) {
                showText(String.format(Locale.US, "User id = %s", data.id))
            }

            override fun onError(t: Throwable) {
                showError(t)
            }
        })

        // read external data for user
        cxenseSdk.getUserExternalData(id, type, object : LoadCallback<List<UserExternalData>> {
            override fun onSuccess(data: List<UserExternalData>) {
                showText(String.format(Locale.US, "We have %d items", data.size))
            }

            override fun onError(t: Throwable) {
                showError(t)
            }
        })

        // read external data for all users with type
        cxenseSdk.getUserExternalData(type, object : LoadCallback<List<UserExternalData>> {
            override fun onSuccess(data: List<UserExternalData>) {
                showText(String.format(Locale.US, "We have %d items", data.size))
            }

            override fun onError(t: Throwable) {
                showError(t)
            }
        })

        // delete external data for user
        cxenseSdk.deleteUserExternalData(UserIdentity(id, type), object : LoadCallback<Void> {
            override fun onSuccess(data: Void) {
                showText("Success")
            }

            override fun onError(t: Throwable) {
                showError(t)
            }
        })

        // update external data for user
        val userExternalData = UserExternalData.Builder(UserIdentity(id, type))
                .addExternalItem("gender", "male")
                .addExternalItem("interests", "football")
                .addExternalItem("sports", "football")
                .build()
        cxenseSdk.setUserExternalData(userExternalData, object : LoadCallback<Void> {
            override fun onSuccess(data: Void) {
                showText("Success")
            }

            override fun onError(t: Throwable) {
                showError(t)
            }
        })

        val builder = PerformanceEvent.Builder(listOf(identity), BuildConfig.SITE_ID, "cxd-origin", "tap")
                .setPrnd(UUID.randomUUID().toString())
                .addCustomParameters(Arrays.asList(
                        CustomParameter("cxd-interests", "TEST"),
                        CustomParameter("cxd-test", "TEST")
                ))
        cxenseSdk.pushEvents(builder.build(), builder.build())
    }

    companion object {
        val TAG = MainActivity::class.java.simpleName
    }

}
