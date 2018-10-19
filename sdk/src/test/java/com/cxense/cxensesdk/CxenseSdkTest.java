package com.cxense.cxensesdk;

import com.cxense.cxensesdk.model.ContentUser;
import com.cxense.cxensesdk.model.Event;
import com.cxense.cxensesdk.model.EventRepository;
import com.cxense.cxensesdk.model.UserExternalData;
import com.cxense.cxensesdk.model.UserIdentity;
import com.cxense.cxensesdk.model.WidgetContext;
import com.cxense.cxensesdk.model.WidgetItem;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.ScheduledExecutorService;

import retrofit2.Call;
import retrofit2.Callback;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2018-10-15).
 */
@PrepareForTest({CxenseConfiguration.class, CxenseSdk.class})
public class CxenseSdkTest extends BaseTest {
    private Call call;
    private LoadCallback callback;
    private ScheduledExecutorService executor;
    private CxenseConfiguration configuration;
    private AdvertisingIdProvider advertisingIdProvider;
    private UserProvider userProvider;
    private EventRepository eventRepository;
    private CxenseSdk cxense;
    private SendTask sendTask;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        call = mock(Call.class);
        callback = mock(LoadCallback.class);
        CxenseApi api = mock(CxenseApi.class, invocation -> call);
        executor = mock(ScheduledExecutorService.class);
        configuration = mock(CxenseConfiguration.class);
        advertisingIdProvider = mock(AdvertisingIdProvider.class);
        userProvider = mock(UserProvider.class);
        eventRepository = mock(EventRepository.class);
        sendTask = mock(SendTask.class);
        cxense = spy(new CxenseSdk(executor, configuration,
                advertisingIdProvider, userProvider, api, mock(ApiErrorParser.class),
                mock(ObjectMapper.class), eventRepository, sendTask));
    }

    private void verifyApiCalled() {
        verify(call).enqueue(any(Callback.class));
    }

    @Test
    public void trackClick() {
        cxense.trackClick(new WidgetItem());
        verifyApiCalled();
    }

    @Test
    public void trackClickUrl() {
        cxense.trackClick("http://example.com");
        verifyApiCalled();
    }

    @Test
    public void loadWidgetRecommendations() {
        doNothing().when(cxense).loadWidgetRecommendations(anyString(), any(WidgetContext.class), isNull(), any(LoadCallback.class));
        WidgetContext widgetContext = new WidgetContext();
        cxense.loadWidgetRecommendations("id", widgetContext, callback);
        verify(cxense).loadWidgetRecommendations("id", widgetContext, null, callback);
    }

    @Test
    public void loadWidgetRecommendationsNullUser() {
        cxense.loadWidgetRecommendations("id", new WidgetContext(), null, callback);
        verify(cxense).getDefaultUser();
        verifyApiCalled();
    }

    @Test
    public void loadWidgetRecommendationsAllArgs() {
        cxense.loadWidgetRecommendations("id", new WidgetContext(), mock(ContentUser.class), callback);
        verifyApiCalled();
    }

    @Test
    public void transform() {
        assertNotNull(cxense.transform(callback));
    }

    @Test
    public void transformFullArgs() {
        Function function = mock(Function.class);
        assertNotNull(cxense.transform(callback, function));
        verify(cxense).transform(any(LoadCallback.class));
    }

    @Test
    public void getUserId() {
        cxense.getUserId();
        verify(userProvider).getUserId();
    }

    @Test
    public void setUserId() {
        String id = "VeryVeryVeryGoodId";
        cxense.setUserId(id);
        verify(userProvider).setUserId(id);
    }

    @Test
    public void getDefaultUserId() {
        cxense.getDefaultUserId();
        verify(advertisingIdProvider).getDefaultUserId();
    }

    @Test
    public void isLimitAdTrackingEnabled() {
        cxense.isLimitAdTrackingEnabled();
        verify(advertisingIdProvider).isLimitAdTrackingEnabled();
    }

    @Test
    public void getConfiguration() {
        assertEquals(configuration, cxense.getConfiguration());
    }

    @Test
    public void getUserSegmentIds() {
        cxense.getUserSegmentIds(Collections.emptyList(), Collections.emptyList(), callback);
        verifyApiCalled();
    }

    @Test
    public void getUserSegmentIdsConsentDisallowed() {
        when(configuration.getConsentOptions()).thenReturn(new HashSet<>(Collections.singletonList(ConsentOption.CONSENT_REQUIRED)));
        cxense.getUserSegmentIds(Collections.emptyList(), Collections.emptyList(), callback);
        verify(configuration).getConsentOptions();
        verify(callback).onSuccess(Collections.emptyList());
    }

    @Test(expected = IllegalArgumentException.class)
    public void getUserSegmentIdsNullIdentities() {
        cxense.getUserSegmentIds(null, Collections.emptyList(), callback);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getUserSegmentIdsNullSitegroups() {
        cxense.getUserSegmentIds(Collections.emptyList(), null, callback);
    }

    @Test
    public void getUser() {
        doNothing().when(cxense).getUser(any(UserIdentity.class), isNull(), isNull(), isNull(), eq(callback));
        cxense.getUser(mock(UserIdentity.class), callback);
        verify(cxense).getUser(any(UserIdentity.class), isNull(), isNull(), isNull(), eq(callback));
    }

    @Test
    public void getUserFullArgs() {
        cxense.getUser(mock(UserIdentity.class), Collections.emptyList(), false, Collections.emptyList(), callback);
        verifyApiCalled();
    }

    @Test(expected = IllegalArgumentException.class)
    public void getUserNullIdentity() {
        cxense.getUser(null, Collections.emptyList(), false, Collections.emptyList(), callback);
    }

    @Test
    public void getUserExternalData() {
        doNothing().when(cxense).getUserExternalData(anyString(), anyString(), eq(callback));
        cxense.getUserExternalData("type", callback);
        verify(cxense).getUserExternalData(null, "type", callback);
    }

    @Test
    public void getUserExternalDataFullArgs() {
        cxense.getUserExternalData("id", "type", callback);
        verifyApiCalled();
    }

    @Test
    public void setUserExternalData() {
        cxense.setUserExternalData(mock(UserExternalData.class), callback);
        verifyApiCalled();
    }

    @Test
    public void deleteUserExternalData() {
        cxense.deleteUserExternalData(mock(UserExternalData.class), callback);
        verifyApiCalled();
    }

    @Test
    public void getUserExternalLink() {
        cxense.getUserExternalLink("id", "type", callback);
        verifyApiCalled();
    }

    @Test
    public void setUserExternalLink() {
        cxense.setUserExternalLink("id", mock(UserIdentity.class), callback);
        verifyApiCalled();
    }

    @Test
    public void putEventTime() {
        doNothing().when(eventRepository).putEventTime(anyString(), anyLong());
        cxense.putEventTime("id", 99);
    }

    @Test
    public void pushEvents() {
        cxense.pushEvents(mock(Event.class));
        verify(executor).execute(any(Runnable.class));
    }

    @Test
    public void trackActiveTime() {
        doNothing().when(cxense).trackActiveTime(anyString(), anyLong());
        cxense.trackActiveTime("id");
        verify(cxense).trackActiveTime("id", 0);
    }

    @Test
    public void trackActiveTimeFullArgs() {
        cxense.trackActiveTime("id", 0);
        verify(executor).execute(any(Runnable.class));
    }

    @Test
    public void getDefaultUser() {
        cxense.getDefaultUser();
        verify(userProvider).getContentUser();
    }

    @Test
    public void flushEventQueue() {
        cxense.flushEventQueue();
        verify(sendTask).run();
    }

    @Test
    public void getQueueStatus() {
        when(eventRepository.getEventStatuses()).thenReturn(Collections.emptyList());
        QueueStatus status = cxense.getQueueStatus();
        assertNotNull(status);
        assertTrue(status.notSentEvents.isEmpty());
        assertTrue(status.sentEvents.isEmpty());
    }

    @Test
    public void setDispatchEventsCallback() {
        cxense.setDispatchEventsCallback(mock(DispatchEventsCallback.class));
        verify(sendTask).setDispatchEventsCallback(any(DispatchEventsCallback.class));
    }

    @Test
    public void executeGetPersistedQuery() {
        cxense.executePersistedQuery("url", "queryId", callback);
        verifyApiCalled();
    }

    @Test
    public void executePostPersistedQuery() {
        cxense.executePersistedQuery("url", "queryId", new Object(), callback);
        verifyApiCalled();
    }
}