package com.bivgroup.exception;

public class DeserializeException extends RuntimeException {

    public DeserializeException(String message) {
        super(message);
    }

    public DeserializeException(Throwable cause) {
        super(cause);
    }

    public DeserializeException(String message, Throwable cause) {
        super(message, cause);
    }
}