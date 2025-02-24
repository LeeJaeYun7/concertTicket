package concert.reservation;

import com.fasterxml.jackson.core.JsonProcessingException;
import concert.domain.concert.entities.ConcertEntity;
import concert.domain.concert.entities.ConcertScheduleEntity;
import concert.domain.concert.entities.ConcertScheduleSeatEntity;
import concert.domain.concert.entities.ConcertSeatGradeEntity;
import concert.domain.concert.entities.enums.ConcertAgeRestriction;
import concert.domain.concert.entities.enums.ConcertScheduleSeatStatus;
import concert.domain.concert.entities.enums.SeatGrade;
import concert.domain.concerthall.entities.ConcertHallEntity;
import concert.domain.concerthall.entities.ConcertHallSeatEntity;
import concert.domain.order.entities.OrderEntity;
import concert.domain.order.entities.ReservationEntity;
import concert.domain.order.entities.dao.OrderEntityDAO;
import concert.domain.order.entities.enums.ReservationStatus;
import concert.domain.order.services.ReservationService;
import concert.domain.order.txservices.OrderTxService;
import concert.domain.order.entities.dao.ReservationEntityDAO;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class OrderTxServiceTest {

  @Mock
  private OrderEntityDAO orderEntityDAO;

  @Mock
  private ReservationEntityDAO reservationEntityDAO;

  @Mock
  private ReservationService reservationService;

  @InjectMocks
  private OrderTxService sut;

  @Nested
  @DisplayName("주문을 생성할 때")
  class 주문을_생성할때 {
    @Test
    @Disabled
    @DisplayName("concertId, concertScheduleId, uuid, 2개의 concertScheduleSeatId가 전달될 때, 주문이 생성된다")
    void concertId_concertScheduleId_uuid_2개의_concertScheduleSeatId가_전달될때_주문이_생성된다() throws JsonProcessingException {
      LocalDate startAt = LocalDate.of(2024, 10, 16);
      LocalDate endAt = LocalDate.of(2024, 10, 18);

      ConcertHallEntity concertHallEntity = ConcertHallEntity.of("KSPO DOME", "서울특별시 송파구 올림픽로 424 (방이동 88-2) 올림픽공원", "02-410-1114", null);
      ConcertEntity concert = ConcertEntity.of("박효신 콘서트", concertHallEntity.getId(), "ballad", 120, ConcertAgeRestriction.OVER_15, startAt, endAt);

      LocalDateTime dateTime = LocalDateTime.of(2024, 10, 16, 22, 30);
      ConcertScheduleEntity concertSchedule = ConcertScheduleEntity.of(concert.getId(), dateTime);

      String uuid = UUID.randomUUID().toString();

      ConcertHallSeatEntity seat1 = ConcertHallSeatEntity.of(concertHallEntity.getId(), 1);
      ConcertHallSeatEntity seat2 = ConcertHallSeatEntity.of(concertHallEntity.getId(), 1);

      ConcertSeatGradeEntity seatGrade = ConcertSeatGradeEntity.of(concert.getId(), SeatGrade.ALL, 100000);

      ConcertScheduleSeatEntity concertScheduleSeat1 = ConcertScheduleSeatEntity.of(seat1.getId(), concertSchedule.getId(), seatGrade.getId(), ConcertScheduleSeatStatus.AVAILABLE);
      ConcertScheduleSeatEntity concertScheduleSeat2 = ConcertScheduleSeatEntity.of(seat2.getId(), concertSchedule.getId(), seatGrade.getId(), ConcertScheduleSeatStatus.AVAILABLE);

      ReservationEntity reservation1 = ReservationEntity.of(1L, concert.getId(), concertScheduleSeat1.getId(), ReservationStatus.ACTIVE, 50000);
      ReservationEntity reservation2 = ReservationEntity.of(1L, concert.getId(), concertScheduleSeat2.getId(), ReservationStatus.ACTIVE, 50000);

      sut.createOrder(concertSchedule.getConcertId(), concertSchedule.getId(), uuid, List.of(concertScheduleSeat1.getId(), concertScheduleSeat2.getId()), 100000);

      verify(orderEntityDAO, times(1)).save(any(OrderEntity.class));
    }
  }
}
