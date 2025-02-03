package concert.domain.order.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum OrderExceptionType {

    ORDER_FAILED(HttpStatus.BAD_REQUEST,"주문이 실패했습니다."),
    PAYMENT_FAILED(HttpStatus.BAD_REQUEST,"결제가 실패했습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    OrderExceptionType(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
