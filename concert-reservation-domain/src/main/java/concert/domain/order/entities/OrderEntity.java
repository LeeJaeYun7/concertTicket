package concert.domain.order.entities;

import concert.domain.order.entities.enums.OrderStatus;
import concert.domain.shared.entities.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "orders")
public class OrderEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "concert_id")
    private long concertId;

    @Column(name = "concert_schedule_id")
    private long concertScheduleId;

    private String uuid;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Column(name = "total_price")
    private long totalPrice;

    @Builder
    public OrderEntity(long concertId, long concertScheduleId, String uuid, OrderStatus orderStatus, long totalPrice) {
        this.concertId = concertId;
        this.concertScheduleId = concertScheduleId;
        this.uuid = uuid;
        this.orderStatus = orderStatus;
        this.totalPrice = totalPrice;
        this.setCreatedAt(LocalDateTime.now());
        this.setUpdatedAt(LocalDateTime.now());
    }

    public static OrderEntity of(long concertId, long concertScheduleId, String uuid, OrderStatus orderStatus, long totalPrice) {
        return OrderEntity.builder()
                          .concertId(concertId)
                          .concertScheduleId(concertScheduleId)
                          .uuid(uuid)
                          .orderStatus(orderStatus)
                          .totalPrice(totalPrice)
                          .build();
    }
}
