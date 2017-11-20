package com.example.cxensesdk;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.cxense.LoadCallback;
import com.cxense.cxensesdk.CxenseConfiguration;
import com.cxense.cxensesdk.CxenseSdk;
import com.cxense.cxensesdk.EventStatus;
import com.cxense.cxensesdk.PerformanceEvent;
import com.cxense.cxensesdk.model.CustomParameter;
import com.cxense.cxensesdk.model.User;
import com.cxense.cxensesdk.model.UserExternalData;
import com.cxense.cxensesdk.model.UserIdentity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements MainAdapter.ItemClickListener {
    public static final String TAG = MainActivity.class.getSimpleName();
    RecyclerView recyclerView;

    private String[] animals = {
            "alligator", "ant", "bear", "bee", "bird", "camel", "cat", "cheetah", "chicken",
            "chimpanzee", "cow", "crocodile", "deer", "dog", "dolphin", "duck", "eagle", "elephant",
            "fish", "fly", "fox", "frog", "giraffe", "goat", "goldfish", "hamster", "hippopotamus",
            "horse", "kangaroo", "kitten", "lion", "lobster", "monkey", "octopus", "owl", "panda",
            "pig", "puppy", "rabbit", "rat", "scorpion", "seal", "shark", "sheep", "snail", "snake",
            "spider", "squirrel", "tiger", "turtle", "wolf", "zebra"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        MainAdapter adapter = new MainAdapter(animals, this);
        recyclerView.setAdapter(adapter);

        CxenseConfiguration config = CxenseSdk.getInstance().getConfiguration();
        config.setDispatchPeriod(CxenseConfiguration.MIN_DISPATCH_PERIOD, TimeUnit.MILLISECONDS);
        config.setApiKey(BuildConfig.API_KEY);
        config.setDmpPushPersistentId(BuildConfig.PERSISTED_ID);
    }

    @Override
    protected void onPause() {
        CxenseSdk.getInstance().setDispatchEventsCallback(null);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        CxenseSdk.getInstance().setDispatchEventsCallback(statuses -> {
            List<String> sent = new ArrayList<>(), notSent = new ArrayList<>();
            for (EventStatus s : statuses) {
                if (s.isSent)
                    sent.add(s.eventId);
                else notSent.add(s.eventId);
            }
            showText(String.format(Locale.getDefault(), "Sent: '%s'\nNot sent: '%s'", TextUtils.join(", ", sent), TextUtils.join(", ", notSent)));
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.run:
                runMethods();
                return true;
            case R.id.flush:
                CxenseSdk.getInstance().flushEventQueue();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(String item) {
        Intent intent = new Intent(this, AnimalActivity.class);
        intent.putExtra(AnimalActivity.ITEM_KEY, item);
        startActivity(intent);
    }

    private void showText(String str) {
        Snackbar.make(recyclerView, str, Snackbar.LENGTH_LONG).show();
    }

    private void showError(Throwable t) {
        Log.e(TAG, t.getMessage(), t);
        showText(t.getMessage());
    }

    private void runMethods() {
        String id = "some_user_id";
        String type = "cxd";
        CxenseSdk cxenseSdk = CxenseSdk.getInstance();
        UserIdentity identity = new UserIdentity(id, type);
        List<UserIdentity> identities = new ArrayList<>();
        identities.add(identity);
        cxenseSdk.getUserSegmentIds(identities, Collections.singletonList(BuildConfig.SITE_ID), new LoadCallback<List<String>>() {
            @Override
            public void onSuccess(List<String> data) {
                showText(TextUtils.join(" ", data));
            }

            @Override
            public void onError(Throwable t) {
                showError(t);
            }
        });
        cxenseSdk.getUser(identity, new LoadCallback<User>() {
            @Override
            public void onSuccess(User data) {
                showText(String.format(Locale.US, "User id = %s", data.getId()));
            }

            @Override
            public void onError(Throwable t) {
                showError(t);
            }
        });

        // read external data for user
        cxenseSdk.getUserExternalData(id, type, new LoadCallback<List<UserExternalData>>() {
            @Override
            public void onSuccess(List<UserExternalData> data) {
                showText(String.format(Locale.US, "We have %d items", data.size()));
            }

            @Override
            public void onError(Throwable t) {
                showError(t);
            }
        });

        // read external data for all users with type
        cxenseSdk.getUserExternalData(type, new LoadCallback<List<UserExternalData>>() {
            @Override
            public void onSuccess(List<UserExternalData> data) {
                showText(String.format(Locale.US, "We have %d items", data.size()));
            }

            @Override
            public void onError(Throwable t) {
                showError(t);
            }
        });

        // delete external data for user
        cxenseSdk.deleteUserExternalData(new UserIdentity(id, type), new LoadCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                showText("Success");
            }

            @Override
            public void onError(Throwable t) {
                showError(t);
            }
        });

        // update external data for user
        UserExternalData userExternalData = new UserExternalData.Builder(new UserIdentity(id, type))
                .addExternalItem("gender", "male")
                .addExternalItem("interests", "football")
                .addExternalItem("sports", "football")
                .build();
        cxenseSdk.setUserExternalData(userExternalData, new LoadCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                showText("Success");
            }

            @Override
            public void onError(Throwable t) {
                showError(t);
            }
        });

        PerformanceEvent.Builder builder = new PerformanceEvent.Builder(Collections.singletonList(identity), BuildConfig.SITE_ID, "cxd-origin", "tap")
                .setPrnd(UUID.randomUUID().toString())
                .addCustomParameters(Arrays.asList(
                        new CustomParameter("cxd-interests", "TEST"),
                        new CustomParameter("cxd-test", "TEST")
                ));
        cxenseSdk.pushEvents(builder.setRnd("123").build(), builder.setRnd("12345").build());
    }

}
