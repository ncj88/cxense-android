package com.cxense.cxensesdk.model;

import java.util.List;

/**
 * Request widget data object for server
 *
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-05).
 */

public final class WidgetRequest {
    public String widgetId;
    public ContentUser user;
    public WidgetContext context;
    public String tag;
    public String prnd;
    public List<String> consent;

    public WidgetRequest() {
    }

    public WidgetRequest(String widgetId, WidgetContext widgetContext, ContentUser widgetUser, final String tag, final String prnd, List<String> consentOptions) {
        this();
        this.widgetId = widgetId;
        user = widgetUser;
        context = widgetContext;
        this.tag = tag;
        this.prnd = prnd;
        consent = consentOptions;
    }
}
