package com.erikmafo.btviewer.exceptions;

public class InvalidCredentialsRecordException extends Exception {
    public InvalidCredentialsRecordException() {
    }

    public InvalidCredentialsRecordException(String message) {
        super(message);
    }

    public InvalidCredentialsRecordException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidCredentialsRecordException(Throwable cause) {
        super(cause);
    }

    public InvalidCredentialsRecordException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
