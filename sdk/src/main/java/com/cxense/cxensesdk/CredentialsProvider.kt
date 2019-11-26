package com.cxense.cxensesdk

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
