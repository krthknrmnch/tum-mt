package com.tum.in.cm.platformservice.exception;

public class RateLimitsException extends Exception {
    public RateLimitsException(String errorMessage) {
        super(errorMessage);
    }
}
