package com.example.concert.common;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private ErrorCode errorCode;

    private Loggable loggable;

    public CustomException(ErrorCode errorCode, Loggable loggable) {
        this.errorCode = errorCode;
        this.loggable = loggable;
    }
}
