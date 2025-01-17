package concert.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConcertRepository extends JpaRepository<Concert, Long> {
    Optional<Concert> findByName(String concertName);
}
