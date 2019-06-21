package com.cxense.cxensesdk.model;

import androidx.annotation.Nullable;

import java.util.List;

/**
 * Response for widget from server.
 *
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-05).
 */

public final class WidgetResponse {
    /**
     * widget items
     */
    @Nullable
    public List<WidgetItem> items;
    /**
     * widget template
     */
    @Nullable
    public String template;
    /**
     * widget style
     */
    @Nullable
    public String style;
}
