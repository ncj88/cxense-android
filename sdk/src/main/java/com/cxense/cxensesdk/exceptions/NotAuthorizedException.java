package com.cxense.cxensesdk.exceptions;

/**
 * Exception that is thrown for HTTP 401 Not Authorized responses
 *
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-07-13).
 */

public class NotAuthorizedException extends CxenseException {
    public NotAuthorizedException() {
        super("Request failed! Please make sure that all the request parameters are valid and uses authorized values.");
    }

    public NotAuthorizedException(String detailMessage) {
        super(detailMessage);
    }

    public NotAuthorizedException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public NotAuthorizedException(Throwable throwable) {
        super(throwable);
    }
}
