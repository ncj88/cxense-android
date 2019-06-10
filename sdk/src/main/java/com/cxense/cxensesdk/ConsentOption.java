package com.cxense.cxensesdk;

import androidx.annotation.NonNull;

public enum ConsentOption {
    CONSENT_REQUIRED("y"),
    PV_ALLOWED("pv"),
    RECS_ALLOWED("recs"),
    SEGMENT_ALLOWED("segment"),
    AD_ALLOWED("ad");

    private String value;

    ConsentOption(@NonNull String value) {
        this.value = value;
    }

    @NonNull
    public String getValue() {
        return value;
    }
}
