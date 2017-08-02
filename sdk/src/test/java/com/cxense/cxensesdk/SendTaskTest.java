package com.cxense.cxensesdk;

import android.database.sqlite.SQLiteDatabaseCorruptException;

import com.cxense.cxensesdk.db.EventRecord;

import org.junit.Before;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.reflect.Whitebox;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.ResponseBody;
import okio.BufferedSource;
import retrofit2.Call;
import retrofit2.Response;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.isNull;
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
    private CxenseConfiguration configuration;
    private CxenseSdk.SendTask sendTask;
    private Call call;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        call = mock(Call.class);
        sendTask = spy(new CxenseSdk.SendTask());
        configuration = spy(new CxenseConfiguration());
        doReturn(false).when(configuration).isRestricted(any());
        CxenseApi api = mock(CxenseApi.class);
        when(api.pushEvents(any())).thenReturn(call);
        when(api.track(any())).thenReturn(call);
        Whitebox.setInternalState(cxense, "apiInstance", api);
        Whitebox.setInternalState(cxense, "configuration", configuration);
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
        verify(cxense, times(2)).putEventRecordInDatabase(any());
    }

    @Test
    public void sendPageViewEvents() throws Exception {
        EventRecord record = new EventRecord();
        record.data = "{}";
        ResponseBody body = mock(ResponseBody.class);
        when(body.source()).thenReturn(mock(BufferedSource.class));
        when(body.byteStream()).thenReturn(new BufferedInputStream(new ByteArrayInputStream(new byte[2])));
        when(call.execute()).thenReturn(Response.success(body));
        when(cxense.unpackMap(isNull())).thenThrow(new IOException());
        sendTask.sendPageViewEvents(Arrays.asList(record, new EventRecord()));
        verify(cxense).putEventRecordInDatabase(any());
    }

    @Test
    public void run() throws Exception {
        List<EventRecord> records = new ArrayList<>();
        when(cxense.getNotSubmittedEvents(anyBoolean())).thenReturn(records);
        sendTask.run();
        verify(cxense).deleteOutdatedEvents();
        verify(sendTask).sendDmpEvents(records);
        verify(sendTask).sendPageViewEvents(records);
    }

    @Test
    public void runOffline() throws Exception {
        when(configuration.getDispatchMode()).thenReturn(CxenseConfiguration.DispatchMode.OFFLINE);
        sendTask.run();
        verify(cxense).deleteOutdatedEvents();
        verify(cxense, never()).getNotSubmittedEvents(anyBoolean());
    }

    @Test
    public void runException() throws Exception {
        doThrow(new SQLiteDatabaseCorruptException()).when(cxense).deleteOutdatedEvents();
        sendTask.run();
    }

}