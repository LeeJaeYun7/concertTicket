package concert.domain.order.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OrderException extends Throwable {
    private final OrderExceptionType orderExceptionType;
}
