package com.cxense.cxensesdk;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Api Error answer
 *
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-05).
 */

final class ApiError {
    @Nullable
    @JsonProperty("error")
    String error;
}
