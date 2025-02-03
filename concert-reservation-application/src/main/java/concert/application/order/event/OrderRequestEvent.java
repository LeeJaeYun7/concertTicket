package concert.application.order.event;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class OrderRequestEvent {

    private long concertId;
    private long concertScheduleId;
    private String uuid;
    private List<Long> concertScheduleSeatIds;
    private long totalPrice;

    @Builder
    public OrderRequestEvent(long concertId, long concertScheduleId, String uuid, List<Long> concertScheduleSeatIds, long totalPrice){
        this.concertId = concertId;
        this.concertScheduleId = concertScheduleId;
        this.uuid = uuid;
        this.concertScheduleSeatIds = concertScheduleSeatIds;
        this.totalPrice = totalPrice;
    }

    public static OrderRequestEvent of(long concertId, long concertScheduleId, String uuid, List<Long> concertScheduleSeatIds, long totalPrice){
        return OrderRequestEvent.builder()
                                  .concertId(concertId)
                                  .concertScheduleId(concertScheduleId)
                                  .uuid(uuid)
                                  .concertScheduleSeatIds(concertScheduleSeatIds)
                                  .totalPrice(totalPrice)
                                  .build();
    }
}
