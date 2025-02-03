package concert.application.order.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentFailedEvent {

    private long concertId;
    private long concertScheduleId;
    private String uuid;
    private List<Long> concertScheduleSeatIds;
    private long totalPrice;
    private String errorMessage;
}
