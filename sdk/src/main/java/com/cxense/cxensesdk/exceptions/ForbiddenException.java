package com.cxense.cxensesdk.exceptions;

/**
 * Exception that is thrown for HTTP 403 Forbidden responses
 *
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-05).
 */
public class ForbiddenException extends CxenseException {
    public ForbiddenException() {
        super("Request failed! Please make sure that all the request parameters are valid and uses authorized values.");
    }

    public ForbiddenException(String detailMessage) {
        super(detailMessage);
    }

    public ForbiddenException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public ForbiddenException(Throwable throwable) {
        super(throwable);
    }
}