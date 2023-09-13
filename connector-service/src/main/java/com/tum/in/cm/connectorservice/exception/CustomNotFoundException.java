package com.tum.in.cm.connectorservice.exception;

public class CustomNotFoundException extends Exception {
    public CustomNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
