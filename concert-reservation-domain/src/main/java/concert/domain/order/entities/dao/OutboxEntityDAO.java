package concert.domain.order.entities.dao;

import concert.domain.order.entities.OutboxEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OutboxEntityDAO extends JpaRepository<OutboxEntity, Long> {

    @Query("SELECT o FROM OutboxEntity o WHERE o.sent = false ORDER BY o.createdAt ASC LIMIT 10")
    List<OutboxEntity> findTop10UnsentEvents();

    Optional<OutboxEntity> findByMessage(String message);
}
