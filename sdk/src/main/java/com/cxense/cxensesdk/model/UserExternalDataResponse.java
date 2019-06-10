package com.cxense.cxensesdk.model;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Response for data associated with the user(s) from server.
 *
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-05).
 */
public final class UserExternalDataResponse {
    @NonNull
    @JsonProperty("data")
    public List<UserExternalData> items;
}
