package concert.domain.concert.entities.dao;

import concert.domain.concert.entities.ConcertSeatGradeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConcertSeatGradeEntityDAO extends JpaRepository<ConcertSeatGradeEntity, Long> {
}
