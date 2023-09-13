package com.tum.in.cm.platformservice.exception;

public class CustomAlreadyExistsException extends Exception {
    public CustomAlreadyExistsException(String errorMessage) {
        super(errorMessage);
    }
}
