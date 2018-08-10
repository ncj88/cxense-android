package com.cxense.cxensesdk.exceptions;

/**
 * Exception that is thrown for HTTP 400 Bad Request responses
 *
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-05).
 */
public class BadRequestException extends CxenseException {
    public BadRequestException() {
        super("Request failed! Please make sure that all the request parameters are valid.");
    }

    public BadRequestException(String detailMessage) {
        super(detailMessage);
    }

    public BadRequestException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public BadRequestException(Throwable throwable) {
        super(throwable);
    }
}