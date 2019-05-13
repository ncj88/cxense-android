package com.example.cxensesdk;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.cxense.cxensesdk.CxenseSdk;
import com.cxense.cxensesdk.EventStatus;
import com.cxense.cxensesdk.model.ConversionEvent;
import com.cxense.cxensesdk.model.PageViewEvent;
import com.cxense.cxensesdk.model.UserIdentity;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class AnimalActivity extends AppCompatActivity {
    public static final String ITEM_KEY = "item";
    private String item;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animal);
        this.item = getIntent().getStringExtra(ITEM_KEY);
        textView = findViewById(R.id.text);
        textView.setText(getString(R.string.item_text, item));
    }

    @Override
    protected void onPause() {
        CxenseSdk.getInstance().trackActiveTime(item);
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
            String message = String.format(Locale.getDefault(), "Sent: '%s'\nNot sent: '%s'", TextUtils.join(", ", sent), TextUtils.join(", ", notSent));
            Snackbar.make(textView, message, Snackbar.LENGTH_LONG).show();
        });
        CxenseSdk.getInstance().pushEvents(
                new PageViewEvent.Builder(BuildConfig.SITE_ID)
                        .setContentId(item)
                        .setEventId(item)
                        .addCustomParameter("xyz-item", item)
                        .build(),
                new ConversionEvent.Builder(Collections.singletonList(new UserIdentity("123456", "cxd")), BuildConfig.SITE_ID, "0ab24abee9a85d869b29f46c837144", ConversionEvent.FUNNEL_TYPE_CONVERT_PRODUCT)
                        .setPrice(12.25)
                        .setRenewalFrequency("1wC")
                        .build()
        );
    }
}
