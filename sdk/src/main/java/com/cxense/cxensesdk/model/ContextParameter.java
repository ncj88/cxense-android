package com.cxense.cxensesdk.model;

import androidx.annotation.NonNull;

/**
 * Context parameter that replace the placeholders are passed from the widget data request.
 *
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-05).
 */

@SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
public final class ContextParameter {
    /**
     * Parameter key
     */
    @NonNull
    public String key = "";

    /**
     * Parameter value
     */
    @NonNull
    public String value = "";

    private ContextParameter() {
    }

    public ContextParameter(@NonNull String key, @NonNull String value) {
        this.key = key;
        this.value = value;
    }
}
