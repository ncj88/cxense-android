package com.cxense.cxensesdk

/**
 * This marker annotation indicates, that Retrofit API method should be with auth header
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Authorized
