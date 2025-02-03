package concert.domain.order;

import concert.domain.order.entities.OrderEntity;
import concert.domain.order.entities.ReservationEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
@Slf4j
public class Order {
    private OrderEntity orderEntity;
    private List<ReservationEntity> reservationEntities;
}
