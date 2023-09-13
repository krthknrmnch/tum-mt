package com.tum.in.cm.platformservice.exception;

public class CustomNotFoundException extends Exception {
    public CustomNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
