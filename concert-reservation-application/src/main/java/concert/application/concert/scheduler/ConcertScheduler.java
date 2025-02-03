package concert.application.concert.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import concert.domain.concert.cache.ConcertCache;
import concert.domain.concert.entities.ConcertEntity;
import concert.domain.order.entities.dao.ReservationEntityDAO;
import concert.domain.shared.utils.TimeProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class ConcertScheduler {

  private final ReservationEntityDAO reservationEntityDAO;
  private final TimeProvider timeProvider;
  private final ConcertCache concertCache;

  @Scheduled(fixedRate = 20000)
  public void updateTop30Concerts() throws JsonProcessingException {
    LocalDateTime now = timeProvider.now();
    LocalDateTime threeDaysAgo = now.minus(Duration.ofHours(72));

    List<ConcertEntity> top30Concerts = reservationEntityDAO.findTop30Concerts(threeDaysAgo);
    concertCache.saveTop30Concerts(top30Concerts);
  }
}
