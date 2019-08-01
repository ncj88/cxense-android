package com.cxense.cxensesdk.model;

import androidx.annotation.NonNull;

import com.cxense.cxensesdk.Preconditions;
import com.google.gson.annotations.SerializedName;

/**
 * Customer-defined parameter object
 *
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-05).
 */

public final class CustomParameter {
    public static final String GROUP = "group";
    public static final String ITEM = "item";
    private static final String TYPE = "type";
    @SerializedName(GROUP)
    String name;
    @SerializedName(TYPE)
    String type;
    @SerializedName(ITEM)
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
    @NonNull
    public String getName() {
        return name;
    }

    /**
     * Gets parameter value
     *
     * @return parameter value
     */
    @NonNull
    public String getItem() {
        return item;
    }
}
