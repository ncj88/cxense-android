package com.cxense.cxensesdk;

import android.text.TextUtils;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import com.cxense.cxensesdk.db.EventRecord;
import com.cxense.cxensesdk.model.CustomParameter;
import com.cxense.cxensesdk.model.Event;
import com.cxense.cxensesdk.model.PerformanceEvent;
import com.cxense.cxensesdk.model.UserIdentity;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2018-09-19).
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public class PerformanceEventConverter extends EventConverter<PerformanceEvent> {
    public static final String CONSENT = "con";
    private final Gson gson;
    private final CxenseConfiguration configuration;

    PerformanceEventConverter(@NonNull Gson gson, @NonNull CxenseConfiguration configuration) {
        this.gson = gson;
        this.configuration = configuration;
    }

    @Override
    public boolean canConvert(@NonNull Event event) {
        return event instanceof PerformanceEvent;
    }

    @Override
    public Map<String, String> toQueryMap(@NonNull PerformanceEvent event) {
        Map<String, String> result = new HashMap<>();
        Date date = event.getTime();
        if (date != null)
            result.put(PerformanceEvent.TIME, "" + date.getTime());
        result.put(PerformanceEvent.PRND, escapeString(event.getPrnd()));
        result.put(PerformanceEvent.RND, escapeString(event.getRnd()));
        result.put(PerformanceEvent.SITE_ID, escapeString(event.getSiteId()));
        result.put(PerformanceEvent.ORIGIN, escapeString(event.getOrigin()));
        result.put(PerformanceEvent.TYPE, escapeString(event.getType()));
        for (CustomParameter cp : event.getCustomParameters()) {
            Pair<String, String> pair = convertInnerObject(PerformanceEvent.CUSTOM_PARAMETERS, cp, CustomParameter.GROUP,
                    CustomParameter.ITEM, CustomParameter::getName, CustomParameter::getItem);
            result.put(pair.first, pair.second);
        }
        for (UserIdentity uid : event.getIdentities()) {
            Pair<String, String> pair = convertInnerObject(PerformanceEvent.USER_IDS, uid, UserIdentity.TYPE, UserIdentity.ID,
                    UserIdentity::getType, UserIdentity::getId);
            result.put(pair.first, pair.second);
        }
        List<String> segments = event.getSegments();
        if (segments != null && !segments.isEmpty())
            result.put(PerformanceEvent.SEGMENT_IDS, TextUtils.join(",", segments));
        String consentOptions = configuration.getConsentOptionsAsString();
        if (consentOptions != null)
            result.put(CONSENT, consentOptions);
        return result;
    }

    @NonNull
    @Override
    public EventRecord toEventRecord(@NonNull PerformanceEvent event)  {
        EventRecord record = new EventRecord();
        record.customId = event.getEventId();
        record.data = gson.toJson(event);
        Date date = event.getTime();
        record.timestamp = date != null ? date.getTime() : System.currentTimeMillis();
        record.ckp = event.getPrnd();
        record.rnd = event.getRnd();
        record.eventType = event.getType();
        return record;
    }

    private <T> Pair<String, String> convertInnerObject(String objectName, T obj, String nameKey, String valueKey,
                                                        Function<T, String> getName, Function<T, String> getValue) {
        List<String> innerData = new ArrayList<>();
        innerData.add(objectName);
        innerData.add(String.format(Locale.getDefault(), "%s:%s", nameKey, getName.apply(obj)));
        innerData.add(valueKey);
        String key = TextUtils.join("/", innerData);
        return new Pair<>(key, getValue.apply(obj));
    }
}
