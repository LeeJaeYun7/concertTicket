package concert.domain.order.entities.dao;

import concert.domain.order.entities.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderEntityDAO extends JpaRepository<OrderEntity, Long> {
}
