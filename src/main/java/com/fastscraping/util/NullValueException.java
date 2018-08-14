package com.fastscraping.util;

public class NullValueException extends IllegalArgumentException {
    public NullValueException() {

    }

    public NullValueException(String message) {
        super(message);
    }

    public NullValueException(String message, Throwable cause) {
        super(message, cause);
    }

    public NullValueException(Throwable cause) {
        super(cause);
    }
}
