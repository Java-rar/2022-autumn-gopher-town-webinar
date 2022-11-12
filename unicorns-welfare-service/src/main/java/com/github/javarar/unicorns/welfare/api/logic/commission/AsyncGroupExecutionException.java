package com.github.javarar.unicorns.welfare.api.logic.commission;

public class AsyncGroupExecutionException extends RuntimeException {
    public AsyncGroupExecutionException(String message) {
        super(message);
    }

    public AsyncGroupExecutionException(Throwable cause) {
        super(cause);
    }
}
