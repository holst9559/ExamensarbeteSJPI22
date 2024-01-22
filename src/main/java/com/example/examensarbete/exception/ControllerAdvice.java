package com.example.examensarbete.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException e) {
        ApiException apiException = new ApiException(
                e.getMessage(),
                HttpStatus.NOT_FOUND,
                getTimestamp()
        );

        return new ResponseEntity<>(apiException, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<Object> handleAuthorizationException(AuthorizationException e) {
        ApiException apiException = new ApiException(
                e.getMessage(),
                HttpStatus.FORBIDDEN,
                getTimestamp()
        );

        return new ResponseEntity<>(apiException, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ResourceAlreadyExistException.class)
    public ResponseEntity<Object> handleResourceAlreadyExistException(ResourceAlreadyExistException e) {
        ApiException apiException = new ApiException(
                e.getMessage(),
                HttpStatus.CONFLICT,
                getTimestamp()
        );

        return new ResponseEntity<>(apiException, HttpStatus.CONFLICT);
    }

    private ZonedDateTime getTimestamp() {
        return ZonedDateTime.now(ZoneId.of("Z"));
    }
}
