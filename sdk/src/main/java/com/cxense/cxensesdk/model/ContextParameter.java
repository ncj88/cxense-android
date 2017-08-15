package com.cxense.cxensesdk.model;

import com.cxense.KeyValueParameter;

/**
 * Context parameter that replace the placeholders are passed from the widget data request.
 *
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-05).
 */

public final class ContextParameter extends KeyValueParameter<String, String> {
    public ContextParameter() {
    }

    public ContextParameter(String key, String value) {
        super(key, value);
    }
}
