package com.cxense.cxensesdk.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collections;
import java.util.Map;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-05).
 */
public final class WidgetItem {

    public WidgetItem(@Nullable String title, @Nullable String url, @NonNull String clickUrl, @NonNull Map<String, Object> properties) {
        this.title = title;
        this.url = url;
        this.clickUrl = clickUrl;
        this.properties = Collections.unmodifiableMap(properties);
    }

    /**
     * Item title, can be null
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    @Nullable
    public final String title;
    /**
     * Item url, can be null.
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    @Nullable
    public final String url;
    /**
     * Click url for item
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    @NonNull
    public final String clickUrl;

    /**
     * Item custom properties (read-only)
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    @NonNull
    public final Map<String, Object> properties;

    /**
     * Returns other custom properties from item
     *
     * @deprecated use {@link #properties} field
     * @return map with custom properties
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    @NonNull
    @Deprecated
    public Map<String, Object> getProperties() {
        return properties;
    }
}
