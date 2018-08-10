package com.cxense.cxensesdk;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Api Error answer
 *
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-05).
 */

final class ApiError {
    @JsonProperty("error")
    String error;
}
