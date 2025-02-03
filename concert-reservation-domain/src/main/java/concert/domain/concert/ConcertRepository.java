package concert.domain.concert;

import concert.domain.concert.entities.dao.ConcertEntityDAO;
import concert.domain.concert.entities.dao.ConcertScheduleEntityDAO;
import concert.domain.concert.entities.dao.ConcertScheduleSeatEntityDAO;
import concert.domain.shared.repositories.DomainRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ConcertRepository implements DomainRepository<Concert> {
    private final ConcertEntityDAO concertEntityDAO;
    private final ConcertScheduleEntityDAO concertScheduleEntityDAO;
    private final ConcertScheduleSeatEntityDAO concertScheduleSeatEntityDAO;
}
