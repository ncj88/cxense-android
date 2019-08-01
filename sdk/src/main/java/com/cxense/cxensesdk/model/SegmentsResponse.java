package com.cxense.cxensesdk.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.List;

/**
 * Response for segments from server.
 *
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-05).
 */

public final class SegmentsResponse {
    /**
     * segments ids
     */
    @NonNull
    @SerializedName("segments")
    public List<String> ids = Collections.emptyList();
}
