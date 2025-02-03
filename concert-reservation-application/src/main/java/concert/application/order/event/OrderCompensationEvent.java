package concert.application.order.event;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class OrderCompensationEvent {

    private long concertId;
    private long concertScheduleId;
    private String uuid;
    private List<Long> concertScheduleSeatIds;
    private long totalPrice;

    @Builder
    public OrderCompensationEvent(long concertId, long concertScheduleId, String uuid, List<Long> concertScheduleSeatIds, long totalPrice){
        this.concertId = concertId;
        this.concertScheduleId = concertScheduleId;
        this.uuid = uuid;
        this.concertScheduleSeatIds = concertScheduleSeatIds;
        this.totalPrice = totalPrice;
    }
}
