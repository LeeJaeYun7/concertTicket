package concert.interfaces.order.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class OrderRequest {

    private String token;
    private long concertScheduleId;
    private List<Long> concertScheduleSeatIds;

    public OrderRequest(String token, long concertScheduleId, List<Long> concertScheduleSeatIds){
        this.token = token;
        this.concertScheduleId = concertScheduleId;
        this.concertScheduleSeatIds = concertScheduleSeatIds;
    }
}
