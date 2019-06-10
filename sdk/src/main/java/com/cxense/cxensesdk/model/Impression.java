package com.cxense.cxensesdk.model;

import androidx.annotation.NonNull;

import com.cxense.cxensesdk.Preconditions;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Describes impression data
 *
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2019-05-14).
 */
@SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
public class Impression {
    @JsonProperty("clickUrl")
    String clickUrl;
    @JsonProperty("visibilitySeconds")
    int seconds;

    private Impression() {
    }

    public Impression(@NonNull String clickUrl, int seconds) {
        this();
        Preconditions.checkStringForNullOrEmpty(clickUrl, "clickUrl");
        Preconditions.check(v -> v <= 0, seconds, "Seconds value should be more than 0");
        this.clickUrl = clickUrl;
        this.seconds = seconds;
    }

    /**
     * Gets click URL
     *
     * @return click URL
     */
    @NonNull
    public String getClickUrl() {
        return clickUrl;
    }

    /**
     * Gets visibility seconds value
     *
     * @return seconds value
     */
    public int getSeconds() {
        return seconds;
    }
}
