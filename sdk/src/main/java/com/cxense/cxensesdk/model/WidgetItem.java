package com.cxense.cxensesdk.model;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-05).
 */
public final class WidgetItem {
    private final Map<String, Object> properties = new HashMap<>();
    /**
     * Item title, can be null
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    @Nullable
    public String title;
    /**
     * Item url, can be null.
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    @Nullable
    public String url;
    /**
     * Click url for item
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    @JsonProperty("click_url")
    public String clickUrl;

    /**
     * Returns other custom properties from item
     *
     * @return map with custom properties
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public Map<String, Object> getProperties() {
        return Collections.unmodifiableMap(properties);
    }

    @JsonAnyGetter
    public Map<String, Object> any() {
        return properties;
    }

    @JsonAnySetter
    public void set(String name, Object value) {
        properties.put(name, value);
    }
}
