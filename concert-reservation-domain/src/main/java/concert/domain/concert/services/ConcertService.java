package concert.domain.concert.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import concert.commons.common.CustomException;
import concert.commons.common.ErrorCode;
import concert.commons.common.Loggable;
import concert.commons.utils.TimeProvider;
import concert.domain.concert.cache.ConcertCache;
import concert.domain.concert.entities.ConcertEntity;
import concert.domain.concert.entities.dao.ConcertEntityDAO;
import concert.domain.reservation.entities.dao.ReservationRepository;
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
  private final ReservationRepository reservationRepository;
  private final ConcertCache concertCache;

  public ConcertEntity getConcertById(long concertId) {
    return concertEntityDAO.findById(concertId)
            .orElseThrow(() -> new CustomException(ErrorCode.CONCERT_NOT_FOUND, Loggable.ALWAYS));
  }

  public ConcertEntity getConcertByName(String concertName) {
    return concertEntityDAO.findByName(concertName)
                            .orElseThrow(() -> new CustomException(ErrorCode.CONCERT_NOT_FOUND, Loggable.ALWAYS));
  }

  public List<Long> getAllConcertIds() {
    return concertEntityDAO.findAll().stream().map(ConcertEntity::getId).collect(Collectors.toList());
  }

  @Transactional
  public void saveTop30ConcertsIntoRedis() throws JsonProcessingException {
    LocalDateTime now = timeProvider.now();
    LocalDateTime threeDaysAgo = now.minus(Duration.ofHours(72));

    List<ConcertEntity> top30concerts = reservationRepository.findTop30Concerts(threeDaysAgo);
    concertCache.saveTop30Concerts(top30concerts);
  }

  @Transactional
  public List<ConcertEntity> getTop30ConcertsFromDB() {
    LocalDateTime now = timeProvider.now();
    LocalDateTime threeDaysAgo = now.minus(Duration.ofHours(72));

    return reservationRepository.findTop30Concerts(threeDaysAgo);
  }

  public List<ConcertEntity> getTop30Concerts() throws JsonProcessingException {

    if (concertCache.findTop30Concerts() != null) {
      return concertCache.findTop30Concerts();
    }

    LocalDateTime now = timeProvider.now();
    LocalDateTime threeDaysAgo = now.minus(Duration.ofHours(72));

    return reservationRepository.findTop30Concerts(threeDaysAgo);
  }
}
