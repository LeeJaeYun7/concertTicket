package com.example.concert.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException ex) {

        if (ex.getLoggable() == Loggable.ALWAYS) {
            logger.error("Custom exception occurred: {}", ex.getMessage(), ex);
        }

         ErrorResponse errorResponse = new ErrorResponse(
                ex.getErrorCode().getHttpStatus(),
                ex.getErrorCode().getMessage()
        );
        return ResponseEntity.status(ex.getErrorCode().getHttpStatus()).body(errorResponse);
    }
}
