package com.github.javarar.unicorns.welfare.api.logic.repository;

public class EmptyResultSet extends RuntimeException {

    public EmptyResultSet() {
    }

    public EmptyResultSet(String message) {
        super(message);
    }

    public EmptyResultSet(String message, Throwable cause) {
        super(message, cause);
    }

    public EmptyResultSet(Throwable cause) {
        super(cause);
    }

    public EmptyResultSet(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
