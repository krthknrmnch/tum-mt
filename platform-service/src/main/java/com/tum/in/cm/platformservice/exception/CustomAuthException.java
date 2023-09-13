package com.tum.in.cm.platformservice.exception;

public class CustomAuthException extends Exception {
    public CustomAuthException(String errorMessage) {
        super(errorMessage);
    }
}
