package com.cxense.cxensesdk.model;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Request object for reporting widgets visibilities to server.
 *
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2019-05-14).
 */
@SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
public class WidgetVisibilityReport {
    @JsonProperty("impressions")
    List<Impression> impressions;

    public WidgetVisibilityReport(@NonNull List<Impression> impressions) {
        this.impressions = new ArrayList<>(impressions);
    }
}
