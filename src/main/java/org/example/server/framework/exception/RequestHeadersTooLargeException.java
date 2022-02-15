package org.example.server.framework.exception;

public class RequestHeadersTooLargeException extends RuntimeException {
    public RequestHeadersTooLargeException() {
    }

    public RequestHeadersTooLargeException(String message) {
        super(message);
    }

    public RequestHeadersTooLargeException(String message, Throwable cause) {
        super(message, cause);
    }

    public RequestHeadersTooLargeException(Throwable cause) {
        super(cause);
    }

    public RequestHeadersTooLargeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
