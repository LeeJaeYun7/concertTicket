package concert.interfaces.order.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class OrderRequest {

    private String uuid;
    private long concertScheduleId;
    private List<Long> concertScheduleSeatIds;

    public OrderRequest(String uuid, long concertScheduleId, List<Long> concertScheduleSeatIds){
        this.uuid = uuid;
        this.concertScheduleId = concertScheduleId;
        this.concertScheduleSeatIds = concertScheduleSeatIds;
    }
}
