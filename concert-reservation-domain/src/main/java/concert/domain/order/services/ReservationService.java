package concert.domain.order.services;

import concert.domain.concert.entities.ConcertScheduleSeatEntity;
import concert.domain.concert.services.ConcertScheduleSeatService;
import concert.domain.concert.services.ConcertSeatGradeService;
import concert.domain.order.entities.ReservationEntity;
import concert.domain.order.entities.dao.ReservationEntityDAO;
import concert.domain.order.entities.enums.ReservationStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationService {

    private final ConcertScheduleSeatService concertScheduleSeatService;
    private final ConcertSeatGradeService concertSeatGradeService;
    private final ReservationEntityDAO reservationEntityDAO;

    public long createReservation(long orderId, long concertId, long concertScheduleSeatId){
        ConcertScheduleSeatEntity concertScheduleSeat = concertScheduleSeatService.getConcertScheduleSeat(concertScheduleSeatId);
        long concertSeatGradePrice = concertSeatGradeService.getConcertSeatGradePrice(concertScheduleSeat.getConcertSeatGradeId());
        ReservationEntity reservation = ReservationEntity.of(orderId, concertId, concertScheduleSeatId, ReservationStatus.ACTIVE, concertSeatGradePrice);
        return reservationEntityDAO.save(reservation).getId();
    }
}
