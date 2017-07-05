package com.example.cxensesdk;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.cxense.cxensesdk.CxenseSdk;
import com.cxense.cxensesdk.PageViewEvent;

public class AnimalActivity extends AppCompatActivity {
    public static final String ITEM_KEY = "item";
    private String item;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animal);
        this.item = getIntent().getStringExtra(ITEM_KEY);
        setContentView(R.layout.activity_animal);
        textView = (TextView) findViewById(R.id.text);
        textView.setText(getString(R.string.item_text, item));
    }

    @Override
    protected void onPause() {
        CxenseSdk.getInstance().trackActiveTime(item);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        CxenseSdk.getInstance().pushEvents(new PageViewEvent.Builder(BuildConfig.SITE_ID, "http://example.com/item")
                .setEventId(item)
                .addCustomParameter("xyz-item", item)
                .build());
    }
}
