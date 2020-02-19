package com.cxense.cxensesdk

/**
 * Interface for providing API credentials.
 * Note that SDK doesn't cache results from these methods, which allows you to dynamically change credentials without replacing provider
 */
interface CredentialsProvider {
    /**
     * Returns username
     *
     * @return username
     */
    fun getUsername(): String

    /**
     * Returns Api key
     *
     * @return api key
     */
    fun getApiKey(): String

    /**
     * Returns persistent query id for pushing DMP Performance events without authorization
     *
     * @return persistent query id
     */
    fun getDmpPushPersistentId(): String
}
