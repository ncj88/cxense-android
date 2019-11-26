package com.cxense.cxensesdk

/**
 * Interface that is implemented to discover when data loading has finished.
 *
 * @param <T> success data type
 */
interface LoadCallback<in T> {
    /**
     * Called when load has completed successfully.
     *
     * @param data result data
     */
    fun onSuccess(data: T)
    /**
     * Called when load has completed with error.
     *
     * @param throwable error
     */
    fun onError(throwable: Throwable)
}
