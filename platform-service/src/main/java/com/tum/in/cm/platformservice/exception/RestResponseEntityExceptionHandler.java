package com.tum.in.cm.platformservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.naming.AuthenticationException;

@ControllerAdvice
@Slf4j
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    //400 Bad Request
    @ExceptionHandler({CustomValidationException.class})
    public ResponseEntity<Object> handleCustomValidationException(
            Exception ex) {
        log.error(ex.getMessage());
        return new ResponseEntity<>(
                "Bad Request", new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    //401 Unauthorized
    @ExceptionHandler({CustomAuthException.class, AuthenticationException.class, AuthenticationCredentialsNotFoundException.class})
    public ResponseEntity<Object> handleCustomAuthException(
            Exception ex) {
        log.error(ex.getMessage());
        return new ResponseEntity<>(
                "Unauthorized", new HttpHeaders(), HttpStatus.UNAUTHORIZED);
    }

    //403 Forbidden
    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity<Object> handleAccessDeniedException(
            Exception ex) {
        log.error(ex.getMessage());
        return new ResponseEntity<>(
                "Forbidden", new HttpHeaders(), HttpStatus.FORBIDDEN);
    }

    //404 Not Found
    @ExceptionHandler({CustomNotFoundException.class})
    public ResponseEntity<Object> handleCustomNotFoundException(
            Exception ex) {
        log.error(ex.getMessage());
        return new ResponseEntity<>(
                "Not Found", new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    //422 Unprocessable Entity
    @ExceptionHandler({CustomAlreadyExistsException.class})
    public ResponseEntity<Object> handleCustomAlreadyExistsException(
            Exception ex) {
        log.error(ex.getMessage());
        return new ResponseEntity<>(
                "Resource Already Exists", new HttpHeaders(), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler({RateLimitsException.class})
    public ResponseEntity<Object> handleRateLimitException(
            Exception ex) {
        log.error(ex.getMessage());
        return new ResponseEntity<>(
                "Measurement creation failed. Measurement/Probe rate limits exceeded.", new HttpHeaders(), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    //500 ISE
    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<Object> handleRuntimeException(
            Exception ex) {
        log.error(ex.getMessage());
        return new ResponseEntity<>(
                "Internal Server Error", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}