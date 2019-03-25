package com.cxense.cxensesdk;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cxense.cxensesdk.db.EventRecord;
import com.cxense.cxensesdk.model.EventDataRequest;
import com.cxense.cxensesdk.model.EventRepository;
import com.cxense.cxensesdk.model.PerformanceEvent;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2018-09-18).
 */
public class SendTask implements Runnable {
    private static final String TAG = "CxenseEventsSender";
    private final CxenseApi cxenseApi;
    private final EventRepository eventRepository;
    private final CxenseConfiguration configuration;
    private final DeviceInfoProvider deviceInfoProvider;
    private final UserProvider userProvider;
    private final ObjectMapper mapper;
    private final PerformanceEventConverter performanceEventConverter;
    private final ApiErrorParser errorParser;
    private DispatchEventsCallback sendCallback;

    public SendTask(@NonNull CxenseApi api, @NonNull EventRepository eventRepository, @NonNull CxenseConfiguration configuration,
                    @NonNull DeviceInfoProvider deviceInfoProvider, @NonNull UserProvider userProvider, @NonNull ObjectMapper mapper,
                    @NonNull PerformanceEventConverter performanceEventConverter, @NonNull ApiErrorParser errorParser,
                    @Nullable DispatchEventsCallback sendCallback) {
        cxenseApi = api;
        this.eventRepository = eventRepository;
        this.configuration = configuration;
        this.deviceInfoProvider = deviceInfoProvider;
        this.userProvider = userProvider;
        this.mapper = mapper;
        this.performanceEventConverter = performanceEventConverter;
        this.errorParser = errorParser;
        this.sendCallback = sendCallback;
    }

    public void setDispatchEventsCallback(DispatchEventsCallback callback) {
        sendCallback = callback;
    }

    private EventStatus createStatus(EventRecord record, Exception exc) {
        return new EventStatus(record.customId, record.isSent, exc);
    }

    void sendDmpEvents(@NonNull List<EventRecord> events) {
        if (events.isEmpty())
            return;
        List<EventStatus> statuses = new ArrayList<>();
        if (configuration.isApiCredentialsProvided()) {
            Exception exception = null;
            try {
                List<String> data = new ArrayList<>();
                for (EventRecord record : events) {
                    data.add(record.data);
                }
                Response<Void> response = cxenseApi.pushEvents(new EventDataRequest(data)).execute();
                if (response.isSuccessful()) {
                    for (EventRecord event : events) {
                        event.isSent = true;
                        eventRepository.putEventRecordInDatabase(event);
                    }
                }
                exception = errorParser.parseError(response);
            } catch (IOException e) {
                exception = e;
            } finally {
                for (EventRecord event : events) {
                    statuses.add(new EventStatus(event.customId, event.isSent, exception));
                }
            }
        } else {
            for (EventRecord event : events) {
                EventStatus status = null;
                try {
                    Map<String, String> data = performanceEventConverter.toQueryMap(mapper.readValue(event.data, PerformanceEvent.class));
                    String segmentsValue = data.get(PerformanceEvent.SEGMENT_IDS);
                    data.remove(PerformanceEvent.SEGMENT_IDS);
                    List<String> segments = new ArrayList<>();
                    if (!TextUtils.isEmpty(segmentsValue)) {
                        segments.addAll(Arrays.asList(segmentsValue.split(",")));
                    }
                    Response<ResponseBody> response = cxenseApi.trackDmpEvent(
                            configuration.getCredentialsProvider().getDmpPushPersistentId(), segments, data
                    ).execute();
                    if (response.isSuccessful()) {
                        event.isSent = true;
                    }
                    eventRepository.putEventRecordInDatabase(event);
                    status = createStatus(event, errorParser.parseError(response));
                } catch (IOException e) {
                    status = createStatus(event, e);
                } finally {
                    statuses.add(status);
                }
            }
        }
        if (sendCallback != null)
            sendCallback.onSend(statuses);
    }

    void sendPageViewEvents(@NonNull List<EventRecord> events) {
        List<EventStatus> statuses = new ArrayList<>();
        for (EventRecord event : events) {
            EventStatus status = null;
            try {
                Map<String, String> data = mapper.readValue(event.data, new TypeReference<HashMap<String, String>>() {
                });
                String ckp = data.get(PageViewEventConverter.CKP);
                String id = userProvider.getUserId();
                if (TextUtils.isEmpty(ckp) && !TextUtils.isEmpty(id)) {
                    data.put(PageViewEventConverter.CKP, id);
                    event.data = mapper.writeValueAsString(data);
                    event.ckp = id;
                }
                Response<ResponseBody> response = cxenseApi.trackInsightEvent(data).execute();
                if (response.isSuccessful()) {
                    event.isSent = true;
                }
                eventRepository.putEventRecordInDatabase(event);
                status = createStatus(event, errorParser.parseError(response));
            } catch (IOException e) {
                status = createStatus(event, e);
            } finally {
                statuses.add(status);
            }
        }
        if (sendCallback != null)
            sendCallback.onSend(statuses);
    }

    @Override
    public void run() {
        try {
            eventRepository.deleteOutdatedEvents(configuration.getOutdatePeriod());
            if (configuration.getDispatchMode() == CxenseConfiguration.DispatchMode.OFFLINE
                    || deviceInfoProvider.getCurrentNetworkStatus().ordinal() < configuration.getMinimumNetworkStatus().ordinal())
                return;

            Set<ConsentOption> consentOptions = configuration.getConsentOptions();
            if (consentOptions.contains(ConsentOption.CONSENT_REQUIRED) && !consentOptions.contains(ConsentOption.PV_ALLOWED))
                return;
            sendPageViewEvents(eventRepository.getNotSubmittedPvEvents());
            sendDmpEvents(eventRepository.getNotSubmittedDmpEvents());

        } catch (Exception e) {
            Log.e(TAG, "Error at sending data", e);
        }
    }
}
