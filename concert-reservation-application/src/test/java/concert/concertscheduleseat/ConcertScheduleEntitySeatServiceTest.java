package concert.concertscheduleseat;

import concert.domain.concert.entities.ConcertEntity;
import concert.domain.concert.entities.ConcertScheduleEntity;
import concert.domain.concert.entities.ConcertScheduleSeatEntity;
import concert.domain.concert.entities.ConcertSeatGradeEntity;
import concert.domain.concert.entities.dao.ConcertScheduleSeatEntityDAO;
import concert.domain.concert.entities.enums.ConcertAgeRestriction;
import concert.domain.concert.entities.enums.ConcertScheduleSeatStatus;
import concert.domain.concert.entities.enums.SeatGrade;
import concert.domain.concerthall.entities.ConcertHallEntity;
import concert.domain.concerthall.entities.ConcertHallSeatEntity;
import concert.domain.concert.services.ConcertScheduleSeatService;
import concert.domain.shared.utils.TimeProvider;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ConcertScheduleEntitySeatServiceTest {

  @Mock
  private TimeProvider timeProvider;

  @Mock
  private ConcertScheduleSeatEntityDAO concertScheduleSeatEntityDAO;

  @InjectMocks
  private ConcertScheduleSeatService sut;

  @Nested
  @DisplayName("예약 가능한 좌석을 조회할 때")
  class 예약_가능한_좌석을_조회할때 {

    @Test
    @DisplayName("concertScheduleId가 전달될 때, 예약 가능한 좌석이 조회된다")
    void concertScheduleId가_전달될때_예약_가능한_좌석이_조회된다() {
      LocalDate startAt = LocalDate.of(2024, 10, 16);
      LocalDate endAt = LocalDate.of(2024, 10, 18);

      ConcertHallEntity concertHallEntity = ConcertHallEntity.of("KSPO DOME", "서울특별시 송파구 올림픽로 424 (방이동 88-2) 올림픽공원", "02-410-1114", null);
      ConcertEntity concert = ConcertEntity.of("박효신 콘서트", concertHallEntity.getId(), "ballad", 120, ConcertAgeRestriction.OVER_15, startAt, endAt);

      LocalDateTime dateTime = LocalDateTime.of(2024, 10, 16, 22, 30);
      ConcertScheduleEntity concertSchedule = ConcertScheduleEntity.of(concert.getId(), dateTime);

      when(timeProvider.now()).thenReturn(LocalDateTime.of(2024, 10, 18, 0, 0));
      LocalDateTime threshold = timeProvider.now().minusMinutes(5);

      ConcertHallSeatEntity seat11 = ConcertHallSeatEntity.of(concertHallEntity.getId(), 11);
      ConcertHallSeatEntity seat22 = ConcertHallSeatEntity.of(concertHallEntity.getId(), 22);

      ConcertSeatGradeEntity allSeatGrade = ConcertSeatGradeEntity.of(concert.getId(), SeatGrade.ALL, 100000);
      ConcertScheduleSeatEntity vipConcertScheduleSeat = ConcertScheduleSeatEntity.of(seat11.getId(), concertSchedule.getId(), allSeatGrade.getId(), ConcertScheduleSeatStatus.AVAILABLE);

      ConcertSeatGradeEntity standingSeatGrade = ConcertSeatGradeEntity.of(concert.getId(), SeatGrade.STANDING, 80000);
      ConcertScheduleSeatEntity standingConcertScheduleSeat = ConcertScheduleSeatEntity.of(seat22.getId(), concertSchedule.getId(), standingSeatGrade.getId(), ConcertScheduleSeatStatus.AVAILABLE);

      List<ConcertScheduleSeatEntity> availableConcertScheduleSeats = List.of(vipConcertScheduleSeat, standingConcertScheduleSeat);

      given(concertScheduleSeatEntityDAO.findAllAvailableConcertScheduleSeatEntities(1L, ConcertScheduleSeatStatus.AVAILABLE, threshold))
              .willReturn(List.of(vipConcertScheduleSeat, standingConcertScheduleSeat));

      List<ConcertScheduleSeatEntity> result = sut.getAllAvailableConcertScheduleSeats(1L);

      assertEquals(result.get(0).getConcertHallSeatId(), availableConcertScheduleSeats.get(0).getConcertHallSeatId());
      assertEquals(result.get(1).getConcertHallSeatId(), availableConcertScheduleSeats.get(1).getConcertHallSeatId());
    }
  }

  @Nested
  @DisplayName("좌석의 업데이트 시각을 최신화할 때")
  class 업데이트_시각을_최신화할때 {

    @Test
    @DisplayName("업데이트 시각을 최신화할 때 성공한다")
    void 업데이트_시각을_최신화할때_성공한다() {
      LocalDate startAt = LocalDate.of(2024, 10, 16);
      LocalDate endAt = LocalDate.of(2024, 10, 18);

      ConcertHallEntity concertHallEntity = ConcertHallEntity.of("KSPO DOME", "서울특별시 송파구 올림픽로 424 (방이동 88-2) 올림픽공원", "02-410-1114", null);
      ConcertEntity concert = ConcertEntity.of("박효신 콘서트", concertHallEntity.getId(), "ballad", 120, ConcertAgeRestriction.OVER_15, startAt, endAt);

      LocalDateTime dateTime = LocalDateTime.of(2024, 10, 16, 22, 30);
      ConcertScheduleEntity concertSchedule = ConcertScheduleEntity.of(concert.getId(), dateTime);

      ConcertHallSeatEntity seat = ConcertHallSeatEntity.of(concertHallEntity.getId(), 1);
      ConcertSeatGradeEntity allSeatGrade = ConcertSeatGradeEntity.of(concert.getId(), SeatGrade.ALL, 100000);
      ConcertScheduleSeatEntity allConcertScheduleSeat = ConcertScheduleSeatEntity.of(seat.getId(), concertSchedule.getId(), allSeatGrade.getId(), ConcertScheduleSeatStatus.AVAILABLE);

      when(concertScheduleSeatEntityDAO.findConcertScheduleSeatEntity(1L)).thenReturn(Optional.of(allConcertScheduleSeat));
      when(timeProvider.now()).thenReturn(LocalDateTime.of(2024, 10, 18, 0, 0));

      sut.changeStatusAndUpdatedAt(1L);

      assertEquals(allConcertScheduleSeat.getUpdatedAt(), LocalDateTime.of(2024, 10, 18, 0, 0));
    }
  }

  @Nested
  @DisplayName("좌석 상태를 업데이트할 때")
  class 좌석_상태를_업데이트할때 {
    @Test
    @DisplayName("좌석 상태를 업데이트할 때 성공한다")
    void 좌석_상태를_업데이트할_때_성공한다() {
      LocalDate startAt = LocalDate.of(2024, 10, 16);
      LocalDate endAt = LocalDate.of(2024, 10, 18);

      ConcertHallEntity concertHallEntity = ConcertHallEntity.of("KSPO DOME", "서울특별시 송파구 올림픽로 424 (방이동 88-2) 올림픽공원", "02-410-1114", null);
      ConcertEntity concert = ConcertEntity.of("박효신 콘서트", concertHallEntity.getId(), "ballad", 120, ConcertAgeRestriction.OVER_15, startAt, endAt);

      LocalDateTime dateTime = LocalDateTime.of(2024, 10, 16, 22, 30);
      ConcertScheduleEntity concertSchedule = ConcertScheduleEntity.of(concert.getId(), dateTime);

      ConcertHallSeatEntity seat = ConcertHallSeatEntity.of(concertHallEntity.getId(), 1);
      ConcertSeatGradeEntity allSeatGrade = ConcertSeatGradeEntity.of(concert.getId(), SeatGrade.ALL, 100000);
      ConcertScheduleSeatEntity allConcertScheduleSeat = ConcertScheduleSeatEntity.of(seat.getId(), concertSchedule.getId(), allSeatGrade.getId(), ConcertScheduleSeatStatus.AVAILABLE);

      when(concertScheduleSeatEntityDAO.findConcertScheduleSeatEntity(1L))
              .thenReturn(Optional.of(allConcertScheduleSeat));

      sut.updateConcertScheduleSeatStatus(1L, ConcertScheduleSeatStatus.RESERVED);

      assertEquals(allConcertScheduleSeat.getStatus(), ConcertScheduleSeatStatus.RESERVED);
    }
  }
}