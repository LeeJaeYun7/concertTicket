package concert.domain.concert.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import concert.domain.concert.cache.ConcertCache;
import concert.domain.concert.entities.ConcertEntity;
import concert.domain.concert.entities.dao.ConcertEntityDAO;
import concert.domain.concert.exceptions.ConcertException;
import concert.domain.concert.exceptions.ConcertExceptionType;
import concert.domain.order.entities.dao.ReservationEntityDAO;
import concert.domain.shared.utils.TimeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConcertService {

  private final TimeProvider timeProvider;
  private final ConcertEntityDAO concertEntityDAO;
  private final ReservationEntityDAO reservationEntityDAO;
  private final ConcertCache concertCache;

  public ConcertEntity getConcertById(long concertId) {
    return concertEntityDAO.findById(concertId)
            .orElseThrow(() -> new ConcertException(ConcertExceptionType.CONCERT_NOT_FOUND));
  }

  public ConcertEntity getConcertByName(String concertName) {
    return concertEntityDAO.findByName(concertName)
                            .orElseThrow(() -> new ConcertException(ConcertExceptionType.CONCERT_NOT_FOUND));
  }

  public List<Long> getAllConcertIds() {
    return concertEntityDAO.findAll().stream().map(ConcertEntity::getId).collect(Collectors.toList());
  }

  @Transactional
  public void saveTop30ConcertsIntoRedis() throws JsonProcessingException {
    LocalDateTime now = timeProvider.now();
    LocalDateTime threeDaysAgo = now.minus(Duration.ofHours(72));

    List<ConcertEntity> top30concerts = reservationEntityDAO.findTop30Concerts(threeDaysAgo);
    concertCache.saveTop30Concerts(top30concerts);
  }

  @Transactional
  public List<ConcertEntity> getTop30ConcertsFromDB() {
    LocalDateTime now = timeProvider.now();
    LocalDateTime threeDaysAgo = now.minus(Duration.ofHours(72));

    return reservationEntityDAO.findTop30Concerts(threeDaysAgo);
  }

  public List<ConcertEntity> getTop30Concerts() throws JsonProcessingException {

    if (concertCache.findTop30Concerts() != null) {
      return concertCache.findTop30Concerts();
    }

    LocalDateTime now = timeProvider.now();
    LocalDateTime threeDaysAgo = now.minus(Duration.ofHours(72));

    return reservationEntityDAO.findTop30Concerts(threeDaysAgo);
  }
}
