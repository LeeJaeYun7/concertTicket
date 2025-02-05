package concert.application.concert.business;

import concert.domain.concert.services.ConcertScheduleService;
import concert.domain.concert.services.ConcertService;
import concert.domain.concerthall.services.ConcertHallSeatService;
import concert.domain.concerthall.entities.ConcertHallSeatEntity;
import concert.domain.concert.services.ConcertScheduleSeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Component
@RequiredArgsConstructor
public class ConcertScheduleSeatApplicationService {

  private final ConcertService concertService;
  private final ConcertHallSeatService concertHallSeatService;
  private final ConcertScheduleService concertScheduleService;
  private final ConcertScheduleSeatService concertScheduleSeatService;

  public List<Long> getActiveConcertScheduleSeatNumbers(long concertScheduleId) {
    concertScheduleService.getConcertScheduleById(concertScheduleId);

    long concertId = concertScheduleService.getConcertScheduleById(concertScheduleId).getConcertId();
    long concertHallId = concertService.getConcertById(concertId).getConcertHallId();
    List<ConcertHallSeatEntity> concertHallSeatEntities = concertHallSeatService.getConcertHallSeatsByConcertHallId(concertHallId);

    return concertScheduleSeatService.getAllAvailableConcertScheduleSeatNumbers(concertScheduleId, concertHallSeatEntities);
  }

  @Transactional
  public void reserveConcertScheduleSeats(List<Long> concertScheduleSeatIds) {
    for(long concertScheduleSeatId: concertScheduleSeatIds){
      concertScheduleSeatService.reserveConcertScheduleSeat(concertScheduleSeatId);
      concertScheduleSeatService.changeStatusAndUpdatedAt(concertScheduleSeatId);
    }
  }
}
