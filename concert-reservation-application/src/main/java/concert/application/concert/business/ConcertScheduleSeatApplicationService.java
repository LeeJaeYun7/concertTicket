package concert.application.concert.business;

import concert.domain.concert.services.ConcertScheduleService;
import concert.domain.concert.services.ConcertService;
import concert.domain.concerthall.services.ConcertHallSeatService;
import concert.domain.concerthall.entities.ConcertHallSeatEntity;
import concert.domain.concert.services.ConcertScheduleSeatService;
import concert.domain.waitingqueue.entities.dao.ActiveQueueDAO;
import concert.infrastructure.distributedlock.dao.TokenConcertScheduleSeatDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Component
@RequiredArgsConstructor
public class ConcertScheduleSeatApplicationService {

  private final ActiveQueueDAO activeQueueDAO;
  private final ConcertService concertService;
  private final ConcertHallSeatService concertHallSeatService;
  private final ConcertScheduleService concertScheduleService;
  private final ConcertScheduleSeatService concertScheduleSeatService;
  private final TokenConcertScheduleSeatDAO tokenConcertScheduleSeatDAO;

  public List<Long> getActiveConcertScheduleSeatNumbers(String uuid, long concertScheduleId) {
    activeQueueDAO.isTokenExistsInActiveQueue(uuid);
    concertScheduleService.getConcertScheduleById(concertScheduleId);

    long concertId = concertScheduleService.getConcertScheduleById(concertScheduleId).getConcertId();
    long concertHallId = concertService.getConcertById(concertId).getConcertHallId();
    List<ConcertHallSeatEntity> concertHallSeatEntities = concertHallSeatService.getConcertHallSeatsByConcertHallId(concertHallId);

    return concertScheduleSeatService.getAllAvailableConcertScheduleSeatNumbers(concertScheduleId, concertHallSeatEntities);
  }

  @Transactional
  public void reserveConcertScheduleSeats(String token, String uuid, List<Long> concertScheduleSeatIds) {
    activeQueueDAO.isTokenExistsInActiveQueue(uuid);

    for(long concertScheduleSeatId: concertScheduleSeatIds){
      concertScheduleSeatService.reserveConcertScheduleSeat(token, concertScheduleSeatId);

      if(isConcertScheduleSeatLockAcquired(token, concertScheduleSeatId)) {
        concertScheduleSeatService.changeStatusAndUpdatedAt(concertScheduleSeatId);
      }
    }
  }

  public boolean isConcertScheduleSeatLockAcquired(String token, long concertScheduleSeatId){
    return tokenConcertScheduleSeatDAO.isExistsToken(Long.toString(concertScheduleSeatId), token);
  }
}
