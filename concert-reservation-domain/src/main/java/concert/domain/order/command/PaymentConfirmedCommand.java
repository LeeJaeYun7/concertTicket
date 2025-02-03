package concert.domain.order.command;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class PaymentConfirmedCommand {

    private long concertId;
    private long concertScheduleId;
    private String uuid;
    private List<Long> concertScheduleSeatIds;
    private long totalPrice;

    @Builder
    public PaymentConfirmedCommand(long concertId, long concertScheduleId, String uuid, List<Long> concertScheduleSeatIds, long totalPrice){
        this.concertId = concertId;
        this.concertScheduleId = concertScheduleId;
        this.uuid = uuid;
        this.concertScheduleSeatIds = concertScheduleSeatIds;
        this.totalPrice = totalPrice;
    }

    public static PaymentConfirmedCommand of(long concertId, long concertScheduleId, String uuid, List<Long> concertScheduleSeatIds, long totalPrice){
        return PaymentConfirmedCommand.builder()
                                           .concertId(concertId)
                                           .concertScheduleId(concertScheduleId)
                                           .uuid(uuid)
                                           .concertScheduleSeatIds(concertScheduleSeatIds)
                                           .totalPrice(totalPrice)
                                           .build();
    }
}
