package com.cxense.cxensesdk;

import android.content.ContentValues;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.cxense.LoadCallback;
import com.cxense.cxensesdk.db.DatabaseHelper;
import com.cxense.cxensesdk.db.EventRecord;
import com.cxense.cxensesdk.model.ContentUser;
import com.cxense.cxensesdk.model.UserExternalData;
import com.cxense.cxensesdk.model.UserIdentity;
import com.cxense.cxensesdk.model.UserPreference;
import com.cxense.cxensesdk.model.WidgetItem;
import com.cxense.cxensesdk.model.WidgetRequest;

import org.junit.Before;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.reflect.Whitebox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;

import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-07-19).
 */
@PrepareForTest({DatabaseHelper.class})
@PowerMockIgnore("javax.net.ssl.*")
public class CxenseSdkTest extends BaseTest {
    private DatabaseHelper databaseHelper;
    private Call call;
    private LoadCallback callback;
    private PackageManager pm;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        databaseHelper = spy(new DatabaseHelper(context));
        call = mock(Call.class);
        CxenseApi api = mock(CxenseApi.class, invocation -> call);
        callback = mock(LoadCallback.class);
        pm = mock(PackageManager.class);

        PackageInfo packageInfo = new PackageInfo();
        packageInfo.versionName = APPVERSION;
        when(pm.getApplicationLabel(any())).thenReturn(APPNAME);
        when(context.getPackageName()).thenReturn(APPNAME);
        when(context.getPackageManager()).thenReturn(pm);
        when(pm.getPackageInfo(anyString(), anyInt())).thenReturn(packageInfo);

        EventRecord record = new EventRecord();
        whenNew(EventRecord.class).withAnyArguments().thenReturn(record);

        Whitebox.setInternalState(cxense, "apiInstance", api);
        Whitebox.setInternalState(cxense, "databaseHelper", databaseHelper);
    }

    @Override
    protected void initCxenseSdk() throws Exception {
        cxense = spy(new CxenseSdk(context));
        Whitebox.setInternalState(CxenseSdk.class, "instance", cxense);
    }

    @Test
    public void init() throws Exception {
        Whitebox.setInternalState(CxenseSdk.class, "instance", (CxenseSdk) null);
        CxenseSdk.init(context);
        assertNotNull(Whitebox.getInternalState(CxenseSdk.class, "instance"));
    }

    @Test
    public void getInstance() throws Exception {
        assertEquals(cxense, CxenseSdk.getInstance());
    }

    @Test(expected = IllegalStateException.class)
    public void getInstanceUninitialized() throws Exception {
        Whitebox.setInternalState(CxenseSdk.class, "instance", (CxenseSdk) null);
        CxenseSdk.getInstance();
    }

    @Test
    public void getBaseUrl() throws Exception {
        assertEquals(BuildConfig.SDK_ENDPOINT, cxense.getBaseUrl());
    }

    @Test
    public void getSdkName() throws Exception {
        assertEquals(BuildConfig.SDK_NAME, cxense.getSdkName());
    }

    @Test
    public void getUserAgent() throws Exception {
        assertThat(cxense.getUserAgent(), startsWith("cx-sdk/"));
    }

    @Test
    public void buildHttpClient() throws Exception {
        OkHttpClient httpClient = cxense.buildHttpClient();
        assertNotNull(httpClient.authenticator());
    }

    @Test
    public void getAuthenticator() throws Exception {
        assertNotNull(cxense.getAuthenticator());
    }

    @Test
    public void updateAuth() throws Exception {
        String username = "user", apiKey = "key";
        CxenseAuthenticator authenticator = spy(new CxenseAuthenticator());
        OkHttpClient httpClient = mock(OkHttpClient.class);
        when(httpClient.authenticator()).thenReturn(authenticator);
        Whitebox.setInternalState(cxense, "okHttpClient", httpClient);
        cxense.updateAuth(username, apiKey);
        verify(authenticator).updateCredentials(anyString(), anyString());
    }

    @Test
    public void updateAuthNullAuthenticator() throws Exception {
        OkHttpClient httpClient = mock(OkHttpClient.class);
        when(httpClient.authenticator()).thenReturn(null);
        Whitebox.setInternalState(cxense, "okHttpClient", httpClient);
        cxense.updateAuth("user", "key");
    }

    @Test
    public void getConfiguration() throws Exception {
        CxenseConfiguration configuration = new CxenseConfiguration();
        Whitebox.setInternalState(cxense, "configuration", configuration);
        assertEquals(configuration, cxense.getConfiguration());
    }

    @Test
    public void getUserSegmentIds() throws Exception {
        cxense.getUserSegmentIds(Collections.emptyList(), Collections.emptyList(), callback);
        verify(call).enqueue(any(Callback.class));
    }

    @Test
    public void getUser() throws Exception {
        doNothing().when(cxense).getUser(any(UserIdentity.class), anyList(), anyBoolean(), anyList(), any(LoadCallback.class));
        cxense.getUser(new UserIdentity("id", "type"), callback);
        verify(cxense).getUser(any(UserIdentity.class), isNull(), isNull(), isNull(), any(LoadCallback.class));
    }

    @Test
    public void getUserFullArgs() throws Exception {
        cxense.getUser(new UserIdentity("id", "type"), new ArrayList<>(), null, new ArrayList<>(), callback);
        verify(call).enqueue(any(Callback.class));
    }

    @Test
    public void getUserExternalData() throws Exception {
        doNothing().when(cxense).getUserExternalData(anyString(), anyString(), any(LoadCallback.class));
        cxense.getUserExternalData("type", callback);
        verify(cxense).getUserExternalData(isNull(), anyString(), any(LoadCallback.class));
    }

    @Test
    public void getUserExternalDataFullArgs() throws Exception {
        cxense.getUserExternalData("id", "type", callback);
        verify(call).enqueue(any(Callback.class));
    }

    @Test
    public void setUserExternalData() throws Exception {
        UserExternalData userExternalData = mock(UserExternalData.class);
        cxense.setUserExternalData(userExternalData, callback);
        verify(call).enqueue(any(Callback.class));
    }

    @Test
    public void deleteUserExternalData() throws Exception {
        UserExternalData userExternalData = mock(UserExternalData.class);
        cxense.deleteUserExternalData(userExternalData, callback);
        verify(call).enqueue(any(Callback.class));
    }

    @Test
    public void getUserExternalLink() throws Exception {
        cxense.getUserExternalLink("id", "type", callback);
        verify(call).enqueue(any(Callback.class));
    }

    @Test
    public void setUserExternalLink() throws Exception {
        cxense.setUserExternalLink("id", new UserIdentity("id", "type"), callback);
        verify(call).enqueue(any(Callback.class));
    }

    @Test
    public void putEvents() throws Exception {
        cxense.putEvents(mock(Event.class), mock(Event.class));
        verify(cxense, times(2)).putEventRecordInDatabase(any());
    }

    @Test
    public void putEventTime() throws Exception {
        String eventId = "id";
        final HashMap<String, String> map = new HashMap<>();
        EventRecord eventRecord = new EventRecord();
        eventRecord.data = "{}";
        doReturn(eventRecord).when(cxense).getEventFromDatabase(anyString());
        doReturn(map).when(cxense).unpackMap(anyString());
        when(cxense.packObject(any())).thenReturn("{}");
        doReturn(0L).when(cxense).putEventRecordInDatabase(any(EventRecord.class));
        cxense.putEventTime(eventId, 0);
        verify(cxense).getEventFromDatabase(eventId);
        verify(cxense).unpackMap(eventRecord.data);
        verify(cxense).packObject(any());
        verify(cxense).putEventRecordInDatabase(any(EventRecord.class));
    }

    @Test
    public void pushEvents() throws Exception {
        cxense.pushEvents(mock(Event.class));
        verifyPrivate(cxense).invoke("postRunnable", any(Runnable.class));
    }

    @Test
    public void trackActiveTime() throws Exception {
        doNothing().when(cxense).trackActiveTime(anyString(), anyLong());
        cxense.trackActiveTime("id");
        verify(cxense).trackActiveTime(anyString(), anyLong());
    }

    @Test
    public void trackActiveTimeFullArgs() throws Exception {
        cxense.trackActiveTime("id", 1234);
        verifyPrivate(cxense).invoke("postRunnable", any(Runnable.class));
    }

    @Test
    public void createWidget() throws Exception {
        String id = "test";
        Widget widget = CxenseSdk.createWidget(id);
        assertEquals(id, Whitebox.getInternalState(widget, "id"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void createWidgetNullId() throws Exception {
        CxenseSdk.createWidget(null);
    }

    @Test
    public void trackClick() throws Exception {
        WidgetItem item = new WidgetItem();
        CxenseSdk.trackClick(item);
        verify(call).enqueue(any(Callback.class));
    }

    @Test
    public void trackClickUrl() throws Exception {
        CxenseSdk.trackClick("http://example.com");
        verify(call).enqueue(any(Callback.class));
    }

    @Test
    public void getDefaultUser() throws Exception {
        String id = "someId";
        when(cxense.getUserId()).thenReturn(id);
        ContentUser user = cxense.getDefaultUser();
        assertNotNull(user);
        assertThat(user.ids, hasEntry("usi", id));
    }

    @Test
    public void getWidgetItems() throws Exception {
        CxenseApi api = mock(CxenseApi.class);
        Call call = mock(Call.class);
        when(api.getWidgetData(any())).thenReturn(call);
        Whitebox.setInternalState(cxense, "apiInstance", api);
        ContentUser user = new ContentUser();
        user.likes = new UserPreference(Arrays.asList("first", "second"), 1);
        WidgetRequest request = new WidgetRequest("id", null, user);
        LoadCallback callback = mock(LoadCallback.class);
        cxense.getWidgetItems(request, callback);
    }

    @Test
    public void initSendTaskSchedule() throws Exception {
        ScheduledExecutorService executor = mock(ScheduledExecutorService.class);
        ScheduledFuture<?> scheduled = mock(ScheduledFuture.class);
        doReturn(scheduled).when(executor).scheduleWithFixedDelay(any(Runnable.class), anyLong(), anyLong(), any(TimeUnit.class));
        Whitebox.setInternalState(cxense, "executor", executor);
        cxense.initSendTaskSchedule();
        verify(executor).scheduleWithFixedDelay(any(Runnable.class), anyLong(), anyLong(), any(TimeUnit.class));
    }

    @Test
    public void getDisplayMetrics() throws Exception {
        DisplayMetrics dm = spy(new DisplayMetrics());
        Resources resources = mock(Resources.class);
        when(context.getResources()).thenReturn(resources);
        when(resources.getDisplayMetrics()).thenReturn(dm);
        assertEquals(dm, cxense.getDisplayMetrics());
    }

    @Test
    public void getApplicationVersion() throws Exception {
        assertEquals(APPVERSION, cxense.getApplicationVersion());
    }

    @Test
    public void getApplicationVersionException() throws Exception {
        when(pm.getPackageInfo(anyString(), anyInt())).thenThrow(new PackageManager.NameNotFoundException());
        assertNull(cxense.getApplicationVersion());
    }

    @Test
    public void getApplicationName() throws Exception {
        assertEquals(APPNAME, cxense.getApplicationName());
    }

    @Test
    public void packObject() throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("1", "2");
        assertEquals("{\"1\":\"2\"}", cxense.packObject(map));
    }

    @Test
    public void unpackMap() throws Exception {
        assertThat(cxense.unpackMap("{\"1\":\"2\"}"), hasEntry("1", "2"));
    }

    @Test
    public void putEventRecordInDatabase() throws Exception {
        doReturn(0L).when(databaseHelper).save(any(EventRecord.class));
        cxense.putEventRecordInDatabase(new EventRecord());
        verify(databaseHelper).save(any(EventRecord.class));
    }

    @Test
    public void deleteOutdatedEvents() throws Exception {
        doReturn(0).when(databaseHelper).delete(anyString(), anyString(), any(String[].class));
        cxense.deleteOutdatedEvents();
        verify(databaseHelper).delete(eq(EventRecord.TABLE_NAME), anyString(), any(String[].class));
    }

    @Test
    public void getNotSubmittedEvents() throws Exception {
        doReturn(Collections.singletonList(new ContentValues())).when(databaseHelper).query(eq(EventRecord.TABLE_NAME), eq(EventRecord.COLUMNS), anyString(), any(String[].class), isNull(), isNull(), anyString());
        assertThat(cxense.getNotSubmittedEvents(true), hasSize(1));
        verify(databaseHelper).query(eq(EventRecord.TABLE_NAME), eq(EventRecord.COLUMNS), anyString(), any(String[].class), isNull(), isNull(), anyString());
    }

    @Test
    public void getEventFromDatabase() throws Exception {
        doReturn(Collections.singletonList(new ContentValues())).when(databaseHelper).query(eq(EventRecord.TABLE_NAME), eq(EventRecord.COLUMNS), anyString(), any(String[].class), isNull(), isNull(), anyString());
        assertNotNull(cxense.getEventFromDatabase("id"));
        verify(databaseHelper).query(eq(EventRecord.TABLE_NAME), eq(EventRecord.COLUMNS), anyString(), any(String[].class), isNull(), isNull(), anyString());
    }

    @Test
    public void getEventFromDatabaseEmpty() throws Exception {
        doReturn(new ArrayList<>()).when(databaseHelper).query(eq(EventRecord.TABLE_NAME), eq(EventRecord.COLUMNS), anyString(), any(String[].class), isNull(), isNull(), anyString());
        assertNull(cxense.getEventFromDatabase("id"));
        verify(databaseHelper).query(eq(EventRecord.TABLE_NAME), eq(EventRecord.COLUMNS), anyString(), any(String[].class), isNull(), isNull(), anyString());
    }

}