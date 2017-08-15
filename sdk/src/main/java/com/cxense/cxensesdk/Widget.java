package com.cxense.cxensesdk;

import com.cxense.LoadCallback;
import com.cxense.cxensesdk.model.ContentUser;
import com.cxense.cxensesdk.model.WidgetContext;
import com.cxense.cxensesdk.model.WidgetItem;
import com.cxense.cxensesdk.model.WidgetRequest;

import java.util.List;

/**
 * Widget class that is used to fetch items from the Cxense Content service for the specified {@link ContentUser} and {@link WidgetContext}.
 *
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-05).
 */
public final class Widget {
    private final String id;

    /**
     * @param id Widget id string.
     */
    public Widget(String id) {
        this.id = id;
    }

    /**
     * Fetch async a list of {@link com.cxense.cxensesdk.model.WidgetItem items} for the given {@link WidgetContext}
     *
     * @param widgetContext the WidgetContext
     * @param listener      listener for returning result
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public void loadItemsAsync(final WidgetContext widgetContext, final LoadCallback<List<WidgetItem>> listener) {
        loadItemsAsync(widgetContext, null, listener);
    }

    /**
     * Fetch async a list of {@link com.cxense.cxensesdk.model.WidgetItem items} for the given {@link WidgetContext} and {@link ContentUser}
     *
     * @param widgetContext the WidgetContext
     * @param user          custom user
     * @param listener      listener for returning result
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess", "SameParameterValue"}) // Public API.
    public void loadItemsAsync(final WidgetContext widgetContext, ContentUser user, final LoadCallback<List<WidgetItem>> listener) {
        CxenseSdk cxense = CxenseSdk.getInstance();
        if (user == null)
            user = cxense.getDefaultUser();
        cxense.getWidgetItems(new WidgetRequest(id, widgetContext, user), listener);
    }
}
