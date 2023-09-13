package com.tum.in.cm.platformservice.exception;

public class CustomValidationException extends Exception {
    public CustomValidationException(String errorMessage) {
        super(errorMessage);
    }
}
