package com.fastscraping.util;

public class MaxBrowsersExceededException extends RuntimeException {
    public MaxBrowsersExceededException() {

    }

    public MaxBrowsersExceededException(String message) {
        super(message);
    }

    public MaxBrowsersExceededException(String message, Throwable cause) {
        super(message, cause);
    }

    public MaxBrowsersExceededException(Throwable cause) {
        super(cause);
    }
}
