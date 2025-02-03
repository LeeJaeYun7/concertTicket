package concert.domain.order;

import concert.domain.order.entities.dao.OrderEntityDAO;
import concert.domain.order.entities.dao.ReservationEntityDAO;
import concert.domain.shared.repositories.DomainRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderRepository implements DomainRepository<Order> {
    private final OrderEntityDAO orderEntityDAO;
    private final ReservationEntityDAO reservationEntityDAO;
}
