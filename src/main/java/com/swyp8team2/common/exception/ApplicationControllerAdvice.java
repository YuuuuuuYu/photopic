package com.swyp8team2.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ApplicationControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handle(MethodArgumentNotValidException e) {
        log.info("MethodArgumentNotValidException", e);
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(ErrorCode.INVALID_ARGUMENT));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Void> handle(HttpRequestMethodNotSupportedException e) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Exception", e);
        return ResponseEntity.internalServerError()
                .body(new ErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR));
    }
}
