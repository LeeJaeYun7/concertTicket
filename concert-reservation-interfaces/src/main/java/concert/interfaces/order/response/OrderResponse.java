package concert.interfaces.order.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class OrderResponse {

    private final String name;
    private final String concertName;
    private final LocalDateTime dateTime;
    private final long price;

    @Builder
    public OrderResponse(String name, String concertName, LocalDateTime dateTime, long price){
        this.name = name;
        this.concertName = concertName;
        this.dateTime = dateTime;
        this.price = price;
    }

    public static OrderResponse of(String name, String concertName, LocalDateTime dateTime, long price){
        return OrderResponse.builder()
                .name(name)
                .concertName(concertName)
                .dateTime(dateTime)
                .price(price)
                .build();
    }
}
