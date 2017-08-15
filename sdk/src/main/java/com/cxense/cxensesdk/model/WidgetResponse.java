package com.cxense.cxensesdk.model;

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
    public List<WidgetItem> items;
    /**
     * widget template
     */
    public String template;
    /**
     * widget style
     */
    public String style;
}
