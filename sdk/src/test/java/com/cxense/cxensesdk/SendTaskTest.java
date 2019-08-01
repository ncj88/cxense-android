package com.cxense.cxensesdk;

import android.database.sqlite.SQLiteDatabaseCorruptException;

import androidx.annotation.NonNull;

import com.cxense.cxensesdk.db.EventRecord;
import com.cxense.cxensesdk.model.EventRepository;
import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import okhttp3.ResponseBody;
import okio.BufferedSource;
import retrofit2.Call;
import retrofit2.Response;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-07-31).
 */
@PrepareForTest({CxenseConfiguration.class})
public class SendTaskTest extends BaseTest {
    private EventRepository eventRepository;
    private CxenseConfiguration configuration;
    private Gson gson;
    private SendTask sendTask;
    private Call call;
    private DispatchEventsCallback sendCallback;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        call = mock(Call.class);
        eventRepository = mock(EventRepository.class);
        configuration = spy(new CxenseConfiguration());
        gson = mock(Gson.class);
        sendCallback = spy(new DispatchEventsCallback() {
            @Override
            public void onSend(@NonNull List<EventStatus> statuses) {
            }
        });
        CxenseApi api = mock(CxenseApi.class);
        DeviceInfoProvider deviceInfoProvider = mock(DeviceInfoProvider.class);
        sendTask = spy(new SendTask(api, eventRepository, configuration, deviceInfoProvider,
                mock(UserProvider.class), gson, mock(PerformanceEventConverter.class),
                mock(ApiErrorParser.class), sendCallback));

        doReturn(CxenseConfiguration.NetworkStatus.WIFI).when(deviceInfoProvider).getCurrentNetworkStatus();
        doReturn(true).when(configuration).isApiCredentialsProvided();
        when(api.pushEvents(any())).thenReturn(call);
        when(api.trackInsightEvent(any())).thenReturn(call);
        when(api.pushConversionEvents(any())).thenReturn(call);
    }

    @Test
    public void sendDmpEvents() throws Exception {
        EventRecord record = new EventRecord();
        record.data = "{}";
        ResponseBody body = mock(ResponseBody.class);
        when(body.source()).thenReturn(mock(BufferedSource.class));
        when(body.byteStream()).thenReturn(new BufferedInputStream(new ByteArrayInputStream(new byte[2])));
        when(call.execute()).thenReturn(Response.success(body));
        sendTask.sendDmpEvents(Arrays.asList(record, new EventRecord()));
        verify(eventRepository, times(2)).putEventRecordInDatabase(any());
        verify(sendCallback).onSend(any());
    }

    @Test
    public void sendDmpEventsUnsuccessful() throws Exception {
        EventRecord record = new EventRecord();
        record.data = "{}";
        ResponseBody body = mock(ResponseBody.class);
        when(call.execute()).thenReturn(Response.error(404, body));
        sendTask.sendDmpEvents(Arrays.asList(record, new EventRecord()));
        verify(sendCallback).onSend(any());
    }

    @Test
    public void sendPageViewEvents() throws Exception {
        EventRecord record = new EventRecord();
        record.data = "{}";
        ResponseBody body = mock(ResponseBody.class);
        when(body.source()).thenReturn(mock(BufferedSource.class));
        when(body.byteStream()).thenReturn(new BufferedInputStream(new ByteArrayInputStream(new byte[2])));
        when(call.execute()).thenReturn(Response.success(body));
        when(gson.fromJson((String) any(), any(Type.class))).thenReturn(Collections.<String, String>emptyMap());
        sendTask.sendPageViewEvents(Arrays.asList(record, new EventRecord()));
        verify(eventRepository, times(2)).putEventRecordInDatabase(any());
        verify(sendCallback).onSend(any());
    }

    @Test
    public void sendConversionEvents() throws Exception {
        EventRecord record = new EventRecord();
        record.data = "{}";
        ResponseBody body = mock(ResponseBody.class);
        when(body.source()).thenReturn(mock(BufferedSource.class));
        when(body.byteStream()).thenReturn(new BufferedInputStream(new ByteArrayInputStream(new byte[2])));
        when(call.execute()).thenReturn(Response.success(body));
        sendTask.sendConversionEvents(Arrays.asList(record, new EventRecord()));
        verify(eventRepository, times(2)).putEventRecordInDatabase(any());
        verify(sendCallback).onSend(any());
    }

    @Test
    public void run() throws Exception {
        List<EventRecord> records = new ArrayList<>();
        when(eventRepository.getNotSubmittedPvEvents()).thenReturn(records);
        sendTask.run();
        verify(eventRepository).deleteOutdatedEvents(anyLong());
        verify(sendTask).sendDmpEvents(records);
        verify(sendTask).sendPageViewEvents(records);
    }

    @Test
    public void runOffline() throws Exception {
        when(configuration.getDispatchMode()).thenReturn(CxenseConfiguration.DispatchMode.OFFLINE);
        sendTask.run();
        verify(eventRepository).deleteOutdatedEvents(anyLong());
        verify(eventRepository, never()).getNotSubmittedPvEvents();
        verify(eventRepository, never()).getNotSubmittedDmpEvents();
    }

    @Test
    public void runException() throws Exception {
        doThrow(new SQLiteDatabaseCorruptException()).when(eventRepository).deleteOutdatedEvents(anyLong());
        sendTask.run();
    }

}