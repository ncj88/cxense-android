package com.cxense.cxensesdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

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
    @JsonProperty("segments")
    public List<String> ids;
}
