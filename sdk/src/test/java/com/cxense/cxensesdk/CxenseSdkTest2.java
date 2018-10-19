package com.cxense.cxensesdk;

import android.content.pm.PackageManager;

import com.cxense.cxensesdk.db.DatabaseHelper;

import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-07-19).
 */
@PrepareForTest({DatabaseHelper.class, Retrofit.class})
@PowerMockIgnore("javax.net.ssl.*")
public class CxenseSdkTest2 {
    private static final String MOCK_URL = "http://example.com";
    private DatabaseHelper databaseHelper;
    private Call call;
    private LoadCallback callback;
    private PackageManager pm;
    private ApiError error;
    private Converter errorConverter;
    private ResponseBody errorBody;
    private CxenseSdk cxense;

//    @Before
//    public void setUp() throws Exception {
//        call = mock(Call.class);
//        CxenseApi api = mock(CxenseApi.class, invocation -> call);
//        cxense = new CxenseSdk(mock(ScheduledExecutorService.class), mock(CxenseConfiguration.class),
//                mock(AdvertisingIdProvider.class), mock(UserProvider.class), api, mock(ApiErrorParser.class),
//                mock(ObjectMapper.class), mock(EventRepository.class), mock(SendTask.class));
//        callback = mock(LoadCallback.class);
//
//        errorConverter = mock(Converter.class);
//        errorBody = mock(ResponseBody.class);
//        error = new ApiError();
//        error.error = "Some text";
//        when(errorConverter.convert(any(ResponseBody.class))).thenReturn(error);
//
//    }
//
//    protected void initCxenseSdk() throws Exception {
//        cxense = spy(new CxenseSdk(context));
//        Whitebox.setInternalState(CxenseSdk.class, "instance", cxense);
//    }
//
//
//    @Test
//    public void buildHttpClient() throws Exception {
//        OkHttpClient client = cxense.buildHttpClient();
//        assertNotNull(client);
//        assertNotNull(client.authenticator());
//        List<Interceptor> interceptors = client.interceptors();
//        assertThat(interceptors, hasSize(greaterThanOrEqualTo(3)));
//        long connectTimeout = client.connectTimeoutMillis();
//        assertThat(connectTimeout, greaterThanOrEqualTo(TimeUnit.SECONDS.toMillis(10)));
//        long readTimeout = client.readTimeoutMillis();
//        assertThat(readTimeout, greaterThanOrEqualTo(TimeUnit.SECONDS.toMillis(10)));
//    }
//
//    @Test
//    public void buildRetrofit() throws Exception {
//        cxense.mapper = mock(ObjectMapper.class);
//        cxense.okHttpClient = mock(OkHttpClient.class);
//        when(cxense.getBaseUrl()).thenReturn(MOCK_URL);
//        Retrofit retrofit = cxense.buildRetrofit();
//        assertNotNull(retrofit);
//        verify(cxense).getConverterFactory();
//        OkHttpClient client = (OkHttpClient) retrofit.callFactory();
//        assertEquals(cxense.okHttpClient, client);
//    }
//
//    @Test
//    public void buildExecutor() throws Exception {
//        assertNotNull(cxense.buildExecutor());
//    }
//
//    @Test
//    public void transform() throws Exception {
//        LoadCallback callback = mock(LoadCallback.class);
//        assertNotNull(cxense.transform(callback));
//    }
//
//    @Test
//    public void transformFullArgs() throws Exception {
//        LoadCallback callback = mock(LoadCallback.class);
//        Function function = mock(Function.class);
//        assertNotNull(cxense.transform(callback, function));
//        verify(cxense).transform(any(LoadCallback.class));
//    }
//
//    @Test
//    public void parseError() throws Exception {
//        mockErrorParsing();
//        Response response = Response.error(400, errorBody);
//        CxenseException result = cxense.parseError(response);
//        assertThat(result, both(isA(CxenseException.class)).and(notNullValue()));
//        assertThat(result.getMessage(), equalTo(error.error));
//    }
//
//    @Test
//    public void parseErrorException() throws Exception {
//        mockErrorParsing();
//        Response response = Response.error(400, errorBody);
//        when(errorConverter.convert(any(ResponseBody.class))).thenThrow(new IOException());
//        CxenseException result = cxense.parseError(response);
//        assertThat(result, both(isA(CxenseException.class)).and(notNullValue()));
//    }
//
//    @Test
//    public void parseErrorResponseSuccesfull() throws Exception {
//        mockErrorParsing();
//        Response response = Response.success(mock(ResponseBody.class));
//        assertNull(cxense.parseError(response));
//    }
//
//    @Test
//    public void onResponse400() throws Exception {
//        checkException(400, BadRequestException.class);
//    }
//
//    @Test
//    public void onResponse401() throws Exception {
//        checkException(401, NotAuthorizedException.class);
//    }
//
//    @Test
//    public void onResponse403() throws Exception {
//        checkException(403, ForbiddenException.class);
//    }
//
//    @Test
//    public void onResponse4XX() throws Exception {
//        checkException(418, CxenseException.class);
//    }
//
//
//    private void mockErrorParsing() throws Exception {
//        cxense.retrofit = mock(Retrofit.class);
//        when(cxense.retrofit.responseBodyConverter(any(Type.class), any(Annotation[].class)))
//                .thenReturn(errorConverter);
//    }
//
//    private void checkException(int code, Class<? extends CxenseException> clazz) throws Exception {
//        mockErrorParsing();
//        Response response = Response.error(code, mock(ResponseBody.class));
//        assertThat(cxense.parseError(response), instanceOf(clazz));
//    }
//
//    @Test
//    public void getUserId() throws Exception {
//        assertEquals(cxense.userId, cxense.getUserId());
//    }
//
//    @Test
//    public void setUserId() throws Exception {
//        String id = "VeryVeryVeryGoodId";
//        cxense.setUserId(id);
//        assertEquals(id, cxense.userId);
//    }
//
//    @SuppressWarnings("ConstantConditions")
//    @Test(expected = IllegalArgumentException.class)
//    public void setUserIdNull() throws Exception {
//        cxense.setUserId(null);
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public void setUserIdBad() throws Exception {
//        cxense.setUserId("BadId");
//    }
//
//    @Test
//    public void getDefaultUserId() throws Exception {
//        String id = "ID";
//        cxense.advertisingInfo = new AdvertisingIdClient.Info(id, false);
//        assertEquals(id, cxense.getDefaultUserId());
//    }
//
//    @Test
//    public void getDefaultUserIdNull() throws Exception {
//        cxense.advertisingInfo = null;
//        assertNull(cxense.getDefaultUserId());
//    }
//
//    @Test
//    public void isLimitAdTrackingEnabled() throws Exception {
//        cxense.advertisingInfo = new AdvertisingIdClient.Info("id", false);
//        assertFalse(cxense.isLimitAdTrackingEnabled());
//    }
//
//    @Test
//    public void isLimitAdTrackingEnabledTrue() throws Exception {
//        cxense.advertisingInfo = new AdvertisingIdClient.Info("id", true);
//        assertTrue(cxense.isLimitAdTrackingEnabled());
//    }
//
//    @Test
//    public void isLimitAdTrackingEnabledNullInfo() throws Exception {
//        cxense.advertisingInfo = null;
//        assertFalse(cxense.isLimitAdTrackingEnabled());
//    }
//
//    @Test
//    public void postRunnable() throws Exception {
//        ScheduledExecutorService executor = mock(ScheduledExecutorService.class);
//        Mockito.doNothing().when(executor).execute(any(Runnable.class));
//        Whitebox.setInternalState(cxense, "executor", executor);
//        Runnable runnable = () -> {
//        };
//        cxense.postRunnable(runnable);
//        verify(executor).execute(runnable);
//    }
//
//    @Test
//    public void init() throws Exception {
//        Whitebox.setInternalState(CxenseSdk.class, "instance", (CxenseSdk) null);
//        CxenseSdk.init(context);
//        assertNotNull(Whitebox.getInternalState(CxenseSdk.class, "instance"));
//    }
//
//    @Test
//    public void getInstance() throws Exception {
//        assertEquals(cxense, CxenseSdk.getInstance());
//    }
//
//    @Test(expected = IllegalStateException.class)
//    public void getInstanceUninitialized() throws Exception {
//        Whitebox.setInternalState(CxenseSdk.class, "instance", (CxenseSdk) null);
//        CxenseSdk.getInstance();
//    }
//
//    @Test
//    public void getBaseUrl() throws Exception {
//        assertEquals(BuildConfig.SDK_ENDPOINT, cxense.getBaseUrl());
//    }
//
//    @Test
//    public void getSdkName() throws Exception {
//        assertEquals(BuildConfig.SDK_NAME, cxense.getSdkName());
//    }
//
//    @Test
//    public void getUserAgent() throws Exception {
//        assertThat(cxense.getUserAgent(), startsWith("cx-sdk/"));
//    }
//
//    @Test
//    public void getAuthenticator() throws Exception {
//        assertNotNull(cxense.getAuthenticator());
//    }
//
//    @Test
//    public void updateAuth() throws Exception {
//        String username = "user", apiKey = "key";
//        CxenseAuthenticator authenticator = spy(new CxenseAuthenticator());
//        OkHttpClient httpClient = mock(OkHttpClient.class);
//        when(httpClient.authenticator()).thenReturn(authenticator);
//        Whitebox.setInternalState(cxense, "okHttpClient", httpClient);
//        cxense.updateAuth(username, apiKey);
//        verify(authenticator).updateCredentials(anyString(), anyString());
//    }
//
//    @Test
//    public void updateAuthNullAuthenticator() throws Exception {
//        OkHttpClient httpClient = mock(OkHttpClient.class);
//        when(httpClient.authenticator()).thenReturn(null);
//        Whitebox.setInternalState(cxense, "okHttpClient", httpClient);
//        cxense.updateAuth("user", "key");
//    }
//
//    @Test
//    public void getConfiguration() throws Exception {
//        CxenseConfiguration configuration = new CxenseConfiguration();
//        Whitebox.setInternalState(cxense, "configuration", configuration);
//        assertEquals(configuration, cxense.getConfiguration());
//    }
//
//    @Test
//    public void executeGetPersistedQuery() throws Exception {
//        cxense.executePersistedQuery("url", "queryId", callback);
//        verify(call).enqueue(any(Callback.class));
//    }
//
//    @Test
//    public void executePostPersistedQuery() throws Exception {
//        cxense.executePersistedQuery("url", "queryId", new Object(), callback);
//        verify(call).enqueue(any(Callback.class));
//    }
//
//    @Test
//    public void getUserSegmentIds() throws Exception {
//        cxense.getUserSegmentIds(Collections.emptyList(), Collections.emptyList(), callback);
//        verify(call).enqueue(any(Callback.class));
//    }
//
//    @Test
//    public void getUser() throws Exception {
//        doNothing().when(cxense).getUser(any(UserIdentity.class), anyList(), anyBoolean(), anyList(),
//                any(LoadCallback.class));
//        cxense.getUser(new UserIdentity("id", "type"), callback);
//        verify(cxense).getUser(any(UserIdentity.class), isNull(), isNull(), isNull(), any(LoadCallback.class));
//    }
//
//    @Test
//    public void getUserFullArgs() throws Exception {
//        cxense.getUser(new UserIdentity("id", "type"), new ArrayList<>(), null, new ArrayList<>(), callback);
//        verify(call).enqueue(any(Callback.class));
//    }
//
//    @Test
//    public void getUserExternalData() throws Exception {
//        doNothing().when(cxense).getUserExternalData(anyString(), anyString(), any(LoadCallback.class));
//        cxense.getUserExternalData("type", callback);
//        verify(cxense).getUserExternalData(isNull(), anyString(), any(LoadCallback.class));
//    }
//
//    @Test
//    public void getUserExternalDataFullArgs() throws Exception {
//        cxense.getUserExternalData("id", "type", callback);
//        verify(call).enqueue(any(Callback.class));
//    }
//
//    @Test
//    public void setUserExternalData() throws Exception {
//        UserExternalData userExternalData = mock(UserExternalData.class);
//        cxense.setUserExternalData(userExternalData, callback);
//        verify(call).enqueue(any(Callback.class));
//    }
//
//    @Test
//    public void deleteUserExternalData() throws Exception {
//        UserExternalData userExternalData = mock(UserExternalData.class);
//        cxense.deleteUserExternalData(userExternalData, callback);
//        verify(call).enqueue(any(Callback.class));
//    }
//
//    @Test
//    public void getUserExternalLink() throws Exception {
//        cxense.getUserExternalLink("id", "type", callback);
//        verify(call).enqueue(any(Callback.class));
//    }
//
//    @Test
//    public void setUserExternalLink() throws Exception {
//        cxense.setUserExternalLink("id", new UserIdentity("id", "type"), callback);
//        verify(call).enqueue(any(Callback.class));
//    }
//
//    @Test
//    public void putEvents() throws Exception {
//        cxense.putEvents(mock(Event.class), mock(Event.class));
//        verify(cxense, times(2)).putEventRecordInDatabase(any());
//    }
//
//    @Test
//    public void putEventTime() throws Exception {
//        String eventId = "id";
//        final HashMap<String, String> map = new HashMap<>();
//        EventRecord eventRecord = new EventRecord();
//        eventRecord.data = "{}";
//        doReturn(eventRecord).when(cxense).getEventFromDatabase(anyString());
//        doReturn(map).when(cxense).unpackMap(anyString());
//        when(cxense.packObject(any())).thenReturn("{}");
//        doReturn(0L).when(cxense).putEventRecordInDatabase(any(EventRecord.class));
//        cxense.putEventTime(eventId, 0);
//        verify(cxense).getEventFromDatabase(eventId);
//        verify(cxense).unpackMap(eventRecord.data);
//        verify(cxense).packObject(any());
//        verify(cxense).putEventRecordInDatabase(any(EventRecord.class));
//    }
//
//    @Test
//    public void pushEvents() throws Exception {
//        cxense.pushEvents(mock(Event.class));
//        verifyPrivate(cxense).invoke("postRunnable", any(Runnable.class));
//    }
//
//    @Test
//    public void trackActiveTime() throws Exception {
//        doNothing().when(cxense).trackActiveTime(anyString(), anyLong());
//        cxense.trackActiveTime("id");
//        verify(cxense).trackActiveTime(anyString(), anyLong());
//    }
//
//    @Test
//    public void trackActiveTimeFullArgs() throws Exception {
//        cxense.trackActiveTime("id", 1234);
//        verifyPrivate(cxense).invoke("postRunnable", any(Runnable.class));
//    }
//
//    @Test
//    public void createWidget() throws Exception {
//        String id = "test";
//        Widget widget = CxenseSdk.createWidget(id);
//        assertEquals(id, Whitebox.getInternalState(widget, "id"));
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public void createWidgetNullId() throws Exception {
//        CxenseSdk.createWidget(null);
//    }
//
//    @Test
//    public void trackClick() throws Exception {
//        WidgetItem item = new WidgetItem();
//        CxenseSdk.trackClick(item);
//        verify(call).enqueue(any(Callback.class));
//    }
//
//    @Test
//    public void trackClickUrl() throws Exception {
//        CxenseSdk.trackClick("http://example.com");
//        verify(call).enqueue(any(Callback.class));
//    }
//
//    @Test
//    public void getDefaultUser() throws Exception {
//        String id = "someId";
//        when(cxense.getUserId()).thenReturn(id);
//        ContentUser user = cxense.getDefaultUser();
//        assertNotNull(user);
//        assertThat(user.ids, hasEntry("usi", id));
//    }
//
//    @Test
//    public void getWidgetItems() throws Exception {
//        CxenseApi api = mock(CxenseApi.class);
//        Call call = mock(Call.class);
//        when(api.getWidgetData(any())).thenReturn(call);
//        Whitebox.setInternalState(cxense, "apiInstance", api);
//        ContentUser user = new ContentUser();
//        user.likes = new UserPreference(Arrays.asList("first", "second"), 1);
//        WidgetRequest request = new WidgetRequest("id", null, user, new ArrayList<>());
//        LoadCallback callback = mock(LoadCallback.class);
//        cxense.getWidgetItems(request, callback);
//    }
//
//    @Test
//    public void initSendTaskSchedule() throws Exception {
//        ScheduledExecutorService executor = mock(ScheduledExecutorService.class);
//        ScheduledFuture<?> scheduled = mock(ScheduledFuture.class);
//        doReturn(scheduled).when(executor).scheduleWithFixedDelay(any(Runnable.class), anyLong(), anyLong(),
//                any(TimeUnit.class));
//        Whitebox.setInternalState(cxense, "executor", executor);
//        cxense.initSendTaskSchedule();
//        verify(executor).scheduleWithFixedDelay(any(Runnable.class), anyLong(), anyLong(), any(TimeUnit.class));
//    }
//
//    @Test
//    public void getDisplayMetrics() throws Exception {
//        DisplayMetrics dm = spy(new DisplayMetrics());
//        Resources resources = mock(Resources.class);
//        when(context.getResources()).thenReturn(resources);
//        when(resources.getDisplayMetrics()).thenReturn(dm);
//        assertEquals(dm, cxense.getDisplayMetrics());
//    }
//
//    @Test
//    public void getApplicationVersion() throws Exception {
//        assertEquals(APPVERSION, cxense.getApplicationVersion());
//    }
//
//    @Test
//    public void getApplicationVersionException() throws Exception {
//        when(pm.getPackageInfo(anyString(), anyInt())).thenThrow(new PackageManager.NameNotFoundException());
//        assertNull(cxense.getApplicationVersion());
//    }
//
//    @Test
//    public void getApplicationName() throws Exception {
//        assertEquals(APPNAME, cxense.getApplicationName());
//    }
//
//    @Test
//    public void packObject() throws Exception {
//        Map<String, String> map = new HashMap<>();
//        map.put("1", "2");
//        assertEquals("{\"1\":\"2\"}", cxense.packObject(map));
//    }
//
//    @Test
//    public void unpackMap() throws Exception {
//        assertThat(cxense.unpackMap("{\"1\":\"2\"}"), hasEntry("1", "2"));
//    }
//
//    @Test
//    public void putEventRecordInDatabase() throws Exception {
//        doReturn(0L).when(databaseHelper).save(any(EventRecord.class));
//        cxense.putEventRecordInDatabase(new EventRecord());
//        verify(databaseHelper).save(any(EventRecord.class));
//    }
//
//    @Test
//    public void deleteOutdatedEvents() throws Exception {
//        doReturn(0).when(databaseHelper).delete(anyString(), anyString(), any(String[].class));
//        cxense.deleteOutdatedEvents();
//        verify(databaseHelper).delete(eq(EventRecord.TABLE_NAME), anyString(), any(String[].class));
//    }
//
//    @Test
//    public void getNotSubmittedEvents() throws Exception {
//        doReturn(Collections.singletonList(new ContentValues())).when(databaseHelper).query(eq(EventRecord.TABLE_NAME),
//                eq(EventRecord.COLUMNS), anyString(), any(String[].class), isNull(), isNull(), anyString());
//        assertThat(cxense.getNotSubmittedEvents(true), hasSize(1));
//        verify(databaseHelper).query(eq(EventRecord.TABLE_NAME), eq(EventRecord.COLUMNS), anyString(),
//                any(String[].class), isNull(), isNull(), anyString());
//    }
//
//    @Test
//    public void getEventFromDatabase() throws Exception {
//        doReturn(Collections.singletonList(new ContentValues())).when(databaseHelper).query(eq(EventRecord.TABLE_NAME),
//                eq(EventRecord.COLUMNS), anyString(), any(String[].class), isNull(), isNull(), anyString());
//        assertNotNull(cxense.getEventFromDatabase("id"));
//        verify(databaseHelper).query(eq(EventRecord.TABLE_NAME), eq(EventRecord.COLUMNS), anyString(),
//                any(String[].class), isNull(), isNull(), anyString());
//    }
//
//    @Test
//    public void getEventFromDatabaseEmpty() throws Exception {
//        doReturn(new ArrayList<>()).when(databaseHelper).query(eq(EventRecord.TABLE_NAME), eq(EventRecord.COLUMNS),
//                anyString(), any(String[].class), isNull(), isNull(), anyString());
//        assertNull(cxense.getEventFromDatabase("id"));
//        verify(databaseHelper).query(eq(EventRecord.TABLE_NAME), eq(EventRecord.COLUMNS), anyString(),
//                any(String[].class), isNull(), isNull(), anyString());
//    }

}