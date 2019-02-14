package com.cxense.cxensesdk;

import android.location.Location;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.cxense.cxensesdk.db.EventRecord;
import com.cxense.cxensesdk.model.Event;
import com.cxense.cxensesdk.model.ExternalUserId;
import com.cxense.cxensesdk.model.PageViewEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2018-09-19).
 */
public class PageViewEventConverter extends EventConverter<PageViewEvent> {
    public static final String TIME = "ltm";
    public static final String RND = "rnd";
    public static final String ACTIVE_RND = "arnd";
    public static final String ACTIVE_TIME = "altm";
    public static final String ACTIVE_SPENT_TIME = "aatm";
    static final String CKP = "ckp";
    static final String EXTERNAL_USER_KEY = "eit";
    static final String EXTERNAL_USER_VALUE = "eid";
    static final String CONSENT = "con";
    /**
     * Default "base url" for url-less mode
     */
    private static final String DEFAULT_URL_LESS_BASE_URL = "http://%s.content.id/%s";
    static final String CUSTOM_PARAMETER_PREFIX = "cp_";
    private static final String CUSTOM_USER_PARAMETER_PREFIX = "cp_u_";
    private static final String DEFAULT_API_VERSION = "1";
    // Map keys constants
    static final String VERSION = "ver";
    static final String TYPE = "typ";
    private static final String ACCOUNT = "acc";
    static final String SITE_ID = "sid";
    static final String LOCATION = "loc";
    static final String REFERRER = "ref";
    private static final String GOAL = "gol";
    private static final String PAGE_NAME = "pgn";
    static final String TIME_OFFSET = "tzo";
    private static final String RESOLUTION = "res";
    private static final String START_RESOLUTION = "wsz";
    private static final String COLOR = "col";
    private static final String DENSITY = "dpr";
    private static final String JAVA = "jav";
    private static final String LANGUAGE = "bln";
    static final String ENCODING = "chs";
    private static final String FLASH = "fls";
    private static final String NEW_USER = "new";
    private static final String LATITUDE = "plat";
    private static final String LONGITUDE = "plon";
    private static final String ACCURACY = "pacc";
    private static final String ALTITUDE = "palt";
    private static final String HEADING = "phed";
    private static final String SPEED = "pspd";
    private final ObjectMapper mapper;
    private final CxenseConfiguration configuration;
    private final DeviceInfoProvider deviceInfoProvider;

    public PageViewEventConverter(ObjectMapper mapper, CxenseConfiguration configuration, DeviceInfoProvider deviceInfoProvider) {
        this.mapper = mapper;
        this.configuration = configuration;
        this.deviceInfoProvider = deviceInfoProvider;
    }

    @Override
    public boolean canConvert(Event event) {
        return event instanceof PageViewEvent;
    }

    @Override
    public Map<String, String> toQueryMap(PageViewEvent event) {
        Calendar calendar = Calendar.getInstance();
        long offset = TimeUnit.MILLISECONDS.toMinutes(calendar.getTimeZone().getOffset(calendar.getTimeInMillis()));
        DisplayMetrics dm = deviceInfoProvider.getDisplayMetrics();
        String resolution = String.format(Locale.US, "%dx%d", dm.widthPixels, dm.heightPixels);
        Locale locale = Locale.getDefault();
        String lang = String.format(Locale.US, "%s_%s", escapeString(locale.getLanguage()),
                escapeString(locale.getCountry()));
        String locationUrl = event.getContentId() != null ? String.format(DEFAULT_URL_LESS_BASE_URL, event.getSiteId(), event.getContentId())
                : event.getLocation();

        Map<String, String> result = new HashMap<>();
        int i = 0;
        for (ExternalUserId userId : event.getExternalUserIds()) {
            result.put(EXTERNAL_USER_KEY + i, userId.key);
            result.put(EXTERNAL_USER_VALUE + i, userId.value);
        }
        if (configuration.isAutoMetaInfoTrackingEnabled()) {
            // automatic app meta gathering
            String appName = deviceInfoProvider.getApplicationName();
            String appVersion = deviceInfoProvider.getApplicationVersion();
            if (!TextUtils.isEmpty(appName))
                result.put(CUSTOM_PARAMETER_PREFIX + "app", appName);
            if (!TextUtils.isEmpty(appVersion))
                result.put(CUSTOM_PARAMETER_PREFIX + "appv", appVersion);
        }
        result.put(SITE_ID, event.getSiteId());
        result.put(VERSION, DEFAULT_API_VERSION);
        result.put(TYPE, event.getType());
        result.put(ACCOUNT, "" + event.getAccountId());
        result.put(LOCATION, locationUrl);
        result.put(REFERRER, escapeString(event.getReferrer()));
        result.put(GOAL, escapeString(event.getGoalId()));
        result.put(PAGE_NAME, escapeString(event.getPageName()));
        result.put(TIME, "" + event.getDate().getTime());
        // The client's timezone.
        result.put(TIME_OFFSET, "" + offset);
        result.put(RESOLUTION, resolution);
        result.put(START_RESOLUTION, resolution);
        // Device color depth.
        result.put(COLOR, "32"); // Android uses ARGB_8888 32bit from version 2.3 (API 10)
        result.put(DENSITY, "" + dm.density);
        result.put(RND, event.getRnd());
        // Is Java enabled
        result.put(JAVA, "0"); // No, we have not Java 😁
        result.put(LANGUAGE, lang);
        result.put(CKP, event.getCkp());
        result.put(ENCODING, "UTF-8");
        // Is Flash enabled?
        result.put(FLASH, "0");
        result.put(NEW_USER, event.isNewUser() ? "1" : "0");
        for (Map.Entry<String, String> entry : event.getCustomParameters().entrySet()) {
            result.put(CUSTOM_PARAMETER_PREFIX + entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, String> entry : event.getCustomUserParameters().entrySet()) {
            result.put(CUSTOM_USER_PARAMETER_PREFIX + entry.getKey(), entry.getValue());
        }
        Location userLocation = event.getUserLocation();
        if (userLocation != null) {
            result.put(LATITUDE, "" + userLocation.getLatitude());
            result.put(LONGITUDE, "" + userLocation.getLongitude());
            if (userLocation.hasAccuracy())
                result.put(ACCURACY, "" + userLocation.getAccuracy());
            if (userLocation.hasAltitude())
                result.put(ALTITUDE, "" + userLocation.getAltitude());
            if (userLocation.hasBearing())
                result.put(HEADING, "" + userLocation.getBearing());
            if (userLocation.hasSpeed())
                result.put(SPEED, "" + userLocation.getSpeed());
        }
        String consent = configuration.getConsentOptionsAsString();
        if (consent != null)
            result.put(CONSENT, consent);
        return result;
    }

    @Override
    public EventRecord toEventRecord(PageViewEvent event) throws JsonProcessingException {
        Map<String, String> eventMap = toQueryMap(event);
        EventRecord record = new EventRecord();
        record.customId = event.getEventId();
        record.data = mapper.writeValueAsString(eventMap);
        record.timestamp = event.getDate().getTime();
        record.ckp = eventMap.get(CKP);
        record.rnd = eventMap.get(RND);
        record.eventType = PageViewEvent.DEFAULT_EVENT_TYPE;
        return record;
    }
}
