package com.omnitech.chai.exception;

/**
 * Created by kay on 6/6/14.
 */
public class ImportException extends RuntimeException {

    public ImportException() {
        super();
    }

    public ImportException(String message) {
        super(message);
    }

    public ImportException(String message, Throwable cause) {
        super(message, cause);
    }

    public ImportException(Throwable cause) {
        super(cause);
    }

}
