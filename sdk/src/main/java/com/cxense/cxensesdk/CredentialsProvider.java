package com.cxense.cxensesdk;

import androidx.annotation.NonNull;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2018-09-17).
 */
public interface CredentialsProvider {
    /**
     * Returns username
     *
     * @return username
     */
    @NonNull
    default String getUsername() {
        return "";
    }

    /**
     * Returns Api key
     *
     * @return api key
     */
    @NonNull
    default String getApiKey() {
        return "";
    }

    /**
     * Returns persistent query id for pushing DMP Performance events without authorization
     *
     * @return persistent query id
     */
    @NonNull
    default String getDmpPushPersistentId() {
        return "";
    }
}
