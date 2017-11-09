package com.cxense.cxensesdk.model;

import android.support.annotation.NonNull;

import com.cxense.Preconditions;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Customer-defined parameter object
 *
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-05).
 */

public final class CustomParameter {
    public static final String GROUP = "group";
    public static final String ITEM = "item";
    private static final String TYPE = "type";
    @JsonProperty(GROUP)
    String name;
    @JsonProperty(TYPE)
    String type;
    @JsonProperty(ITEM)
    String item;

    private CustomParameter() {
    }

    /**
     * Create new instance of customer-defined parameter
     *
     * @param name Parameter name, e.g., "campaign", "adspace" or "creative".
     * @param item Parameter value, e.g. "sale", "42".
     */
    public CustomParameter(@NonNull String name, @NonNull String item) {
        Preconditions.checkStringForNullOrEmpty(name, "name");
        Preconditions.checkStringForNullOrEmpty(item, "item");
        this.name = name;
        this.item = item;
    }

    /**
     * Gets parameter name
     *
     * @return parameter name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets parameter value
     *
     * @return parameter value
     */
    public String getItem() {
        return item;
    }
}
