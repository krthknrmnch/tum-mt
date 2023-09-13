package com.tum.in.cm.connectorservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Slf4j
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    //404 Not Found
    @ExceptionHandler({CustomNotFoundException.class})
    public ResponseEntity<Object> handleCustomNotFoundException(
            Exception ex) {
        log.error(ex.getMessage());
        return new ResponseEntity<>(
                "Not Found", new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    //500 ISE
    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<Object> handleRuntimeException(
            Exception ex) {
        log.error(ex.getMessage());
        return new ResponseEntity<>(
                "Internal Server Error", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //417 Expectation Failed
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Object> handleMaxSizeException(
            Exception ex) {
        log.error(ex.getMessage());
        return new ResponseEntity<>(
                "File too large", new HttpHeaders(), HttpStatus.EXPECTATION_FAILED);
    }
}
