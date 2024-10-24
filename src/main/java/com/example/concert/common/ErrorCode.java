package com.example.concert.common;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    //== 400 ==//
    NOT_VALID_TOKEN(HttpStatus.BAD_REQUEST,"유효한 토큰이 아닙니다."),
    NOT_VALID_SEAT(HttpStatus.BAD_REQUEST,"예약 가능한 좌석이 아닙니다."),

    TOKEN_ALREADY_EXISTS(HttpStatus.BAD_REQUEST,"같은 대기열에 토큰이 이미 존재합니다."),
    INSUFFICIENT_BALANCE(HttpStatus.BAD_REQUEST,"잔고가 부족합니다."),

    SEAT_RESERVATION_EXPIRED(HttpStatus.BAD_REQUEST,"좌석 예약 선점 시간이 초과되었습니다. 다시 시도해 주세요."),

    //== 404 ==//
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 사용자를 찾을 수 없습니다."),
    CONCERT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 콘서트를 찾을 수 없습니다."),
    CONCERT_SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 콘서트 스케줄을 찾을 수 없습니다."),
    SEAT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 좌석을 찾을 수 없습니다."),
    TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 토큰을 찾을 수 없습니다."),

    WAITING_QUEUE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 토큰을 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }
}