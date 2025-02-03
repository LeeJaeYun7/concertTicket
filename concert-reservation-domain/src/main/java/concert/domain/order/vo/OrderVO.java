package concert.domain.order.vo;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class OrderVO {

    private final String name;
    private final String concertName;
    private final LocalDateTime dateTime;
    private final long totalPrice;

    @Builder
    public OrderVO(String name, String concertName, LocalDateTime dateTime, long totalPrice) {
        this.name = name;
        this.concertName = concertName;
        this.dateTime = dateTime;
        this.totalPrice = totalPrice;
    }
    public static OrderVO of(String name, String concertName, LocalDateTime dateTime, long totalPrice) {
        return OrderVO.builder()
                       .name(name)
                       .concertName(concertName)
                       .dateTime(dateTime)
                       .totalPrice(totalPrice)
                       .build();
    }
}
