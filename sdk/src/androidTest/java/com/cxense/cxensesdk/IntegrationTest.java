package com.cxense.cxensesdk;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.text.TextUtils;

import com.cxense.cxensesdk.model.CustomParameter;
import com.cxense.cxensesdk.model.UserIdentity;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.Assert.assertThat;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-08-10).
 */
@RunWith(AndroidJUnit4.class)
public class IntegrationTest {
    private static final String API_USER = BuildConfig.CX_USER;
    private static final String API_KEY = BuildConfig.CX_KEY;
    private static final String SITE_ID = BuildConfig.CX_SITE_ID;
    private static final String JSON_QUERY = "{\"start\": %d, \"stop\": %d, \"siteId\": \"%s\", \"count\": 1000, \"fields\":[\"eventId\", \"userId\", \"customParameters\"]%s}";
    private final Object syncObject = new Object();
    private CxenseSdk cxense;
    private OkHttpClient okHttpClient;
    private long start, end;

    @Before
    public void setUp() throws Exception {
        CxenseSdk.init(InstrumentationRegistry.getTargetContext());
        cxense = CxenseSdk.getInstance();
        cxense.sendTask = new NotifyingSendTask(syncObject);
        cxense.initSendTaskSchedule();
        cxense.setUserId("VERY-RANDOM-USER-ID");
        final CxenseConfiguration configuration = cxense.getConfiguration();
        configuration.setDispatchPeriod(CxenseConfiguration.MIN_DISPATCH_PERIOD, TimeUnit.MILLISECONDS);
        configuration.setUsername(API_USER);
        configuration.setApiKey(API_KEY);

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .authenticator(new CxenseAuthenticator(API_USER, API_KEY))
                .build();
        start = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        end = start + TimeUnit.HOURS.toMillis(1);
    }

    private void sendAndCheck(String url, String jsonFilter, Event... events) throws Exception {
        cxense.putEvents(events);
        synchronized (syncObject) {
            syncObject.wait();
        }
        // Wait for backend....
        Thread.sleep(5000);
        String json = String.format(Locale.US, JSON_QUERY, start, end, SITE_ID, TextUtils.isEmpty(jsonFilter) ? "" : ", \"filters\": " + jsonFilter);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Response response = okHttpClient.newCall(request).execute();
        JSONObject obj = new JSONObject(response.body().string());
        JSONArray arr = obj.getJSONArray("events");
        assertThat(arr.length(), greaterThanOrEqualTo(events.length));
    }

    @Test
    public void trackPageViewEvent() throws Exception {
        String item = "bear";
        sendAndCheck("https://api.cxense.com/traffic/data", "[{\"type\":\"event\", \"group\":\"os\", \"item\":\"Android\"}]", new PageViewEvent.Builder(SITE_ID)
                .setContentId(item)
                .setEventId(item)
                .addCustomParameter("xyz-item", item)
                .build());
    }

    @Test
    public void trackPerformanceEvent() throws Exception {
        UserIdentity identity = new UserIdentity("VERY-RANDOM-USER-ID", "cxd");
        PerformanceEvent.Builder builder = new PerformanceEvent.Builder(Collections.singletonList(identity), SITE_ID, "cxd-origin", "tap")
                .addCustomParameter(new CustomParameter("cxd-interests", "TEST"));
        sendAndCheck("https://api.cxense.com/dmp/traffic/data", "", builder.setPrnd("123").build(), builder.setPrnd("12345").build());
    }
}
