package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.io.FileNotFoundException;
import java.util.NoSuchElementException;

/**
 * 전역 Exception Handler
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({NoSuchElementException.class, FileNotFoundException.class})
    public ResponseEntity<?> handleException() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
