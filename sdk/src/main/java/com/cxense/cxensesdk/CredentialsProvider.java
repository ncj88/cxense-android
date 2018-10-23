package com.cxense.cxensesdk;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2018-09-17).
 */
public interface CredentialsProvider {
    /**
     * Returns username
     *
     * @return username
     */
    default String getUsername() {
        return "";
    }

    /**
     * Returns Api key
     *
     * @return api key
     */
    default String getApiKey() {
        return "";
    }

    /**
     * Returns persistent query id for pushing DMP Performance events without authorization
     *
     * @return persistent query id
     */
    default String getDmpPushPersistentId() {
        return "";
    }
}
