package com.tum.in.cm.probeservice.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomProcessExecutionException extends Exception {
    public CustomProcessExecutionException(String errorMessage) {
        super(errorMessage);
        log.error(errorMessage);
    }
}
