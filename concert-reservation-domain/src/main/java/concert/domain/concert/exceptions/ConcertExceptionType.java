package concert.domain.concert.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ConcertExceptionType {

    CONCERT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 콘서트를 찾을 수 없습니다."),
    CONCERT_SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 콘서트 스케줄을 찾을 수 없습니다."),
    CONCERT_SCHEDULE_SEAT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 좌석을 찾을 수 없습니다."),
    CONCERT_SEAT_GRADE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 좌석 등급을 찾을 수 없습니다."),
    SEAT_RESERVATION_EXPIRED(HttpStatus.BAD_REQUEST,"좌석 예약 선점 시간이 초과되었습니다. 다시 시도해 주세요."),
    NOT_VALID_SEAT(HttpStatus.BAD_REQUEST,"예약 가능한 좌석이 아닙니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ConcertExceptionType(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
