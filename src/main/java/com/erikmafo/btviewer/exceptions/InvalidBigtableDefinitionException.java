package com.erikmafo.btviewer.exceptions;

public class InvalidBigtableDefinitionException extends Exception {

    public InvalidBigtableDefinitionException() {
    }

    public InvalidBigtableDefinitionException(String message) {
        super(message);
    }

    public InvalidBigtableDefinitionException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidBigtableDefinitionException(Throwable cause) {
        super(cause);
    }

    public InvalidBigtableDefinitionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
