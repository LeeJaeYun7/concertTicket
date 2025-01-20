package concert.domain.concerthallseat.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConcertHallSeatRepository extends JpaRepository<ConcertHallSeat, Long> {
}
