package com.cxense.cxensesdk;

public enum ConsentOption {
    CONSENT_REQUIRED("y"),
    PV_ALLOWED("pv"),
    RECS_ALLOWED("recs"),
    SEGMENT_ALLOWED("segment"),
    AD_ALLOWED("ad");

    private String value;

    ConsentOption(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
