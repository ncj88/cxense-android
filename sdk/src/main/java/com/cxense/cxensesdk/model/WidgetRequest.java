package com.cxense.cxensesdk.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

import java.util.List;

/**
 * Request widget data object for server
 *
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-05).
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public final class WidgetRequest {
    public String widgetId;
    public ContentUser user;
    public WidgetContext context;
    public String tag;
    public String prnd;
    public List<String> consent;

    public WidgetRequest() {
    }

    public WidgetRequest(@NonNull String widgetId, @NonNull WidgetContext widgetContext,
                         @NonNull ContentUser widgetUser, @Nullable final String tag,
                         @Nullable final String prnd, @NonNull List<String> consentOptions) {
        this();
        this.widgetId = widgetId;
        user = widgetUser;
        context = widgetContext;
        this.tag = tag;
        this.prnd = prnd;
        consent = consentOptions;
    }
}
