package com.cxense.cxensesdk.exceptions;

/**
 * Base class for SDK exceptions
 *
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-05).
 */
public class CxenseException extends RuntimeException {
    public CxenseException() {
    }

    public CxenseException(String detailMessage) {
        super(detailMessage);
    }

    public CxenseException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public CxenseException(Throwable throwable) {
        super(throwable);
    }
}
