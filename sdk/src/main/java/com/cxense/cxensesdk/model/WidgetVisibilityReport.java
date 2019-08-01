package com.cxense.cxensesdk.model;

import androidx.annotation.NonNull;

import com.cxense.cxensesdk.Preconditions;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Request object for reporting widgets visibilities to server.
 *
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2019-05-14).
 */
@SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
public class WidgetVisibilityReport {
    @SerializedName("impressions")
    List<Impression> impressions;

    public WidgetVisibilityReport(@NonNull Collection<Impression> impressions) {
        Preconditions.checkForNull(impressions, "impressions");
        this.impressions = new ArrayList<>(impressions);
    }
}
