package concert.concertschedule;

import concert.application.concert.business.ConcertScheduleApplicationService;
import concert.domain.concert.entities.ConcertEntity;
import concert.domain.concert.entities.ConcertScheduleEntity;
import concert.domain.concert.entities.ConcertScheduleSeatEntity;
import concert.domain.concert.entities.ConcertSeatGradeEntity;
import concert.domain.concert.entities.enums.ConcertScheduleSeatStatus;
import concert.domain.concert.entities.enums.SeatGrade;
import concert.domain.concert.services.ConcertScheduleService;
import concert.domain.concert.services.ConcertService;
import concert.domain.concert.entities.enums.ConcertAgeRestriction;
import concert.domain.concerthall.entities.ConcertHallEntity;
import concert.domain.concerthall.entities.ConcertHallSeatEntity;
import org.junit.jupiter.api.BeforeEach;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ConcertScheduleApplicationServiceTest {

  @Mock
  private ConcertService concertService;

  @Mock
  private ConcertScheduleService concertScheduleService;

  @InjectMocks
  private ConcertScheduleApplicationService sut;

  private long concertId;
  private ConcertEntity concert;
  private ConcertHallEntity concertHallEntity;

  private ConcertHallSeatEntity seatNumber1;

  private ConcertHallSeatEntity seatNumber100;
  private ConcertScheduleEntity firstSchedule;
  private ConcertScheduleEntity secondSchedule;
  private ConcertScheduleSeatEntity firstALLConcertScheduleSeat;
  private ConcertScheduleSeatEntity firstSTANDINGConcertScheduleSeat;
  private ConcertScheduleSeatEntity secondALLConcertScheduleSeat;
  private ConcertScheduleSeatEntity secondSTANDINGConcertScheduleSeat;

  @BeforeEach
  void setUp() {
    concertId = 1L;
    LocalDate startAt = LocalDate.of(2024, 10, 16);
    LocalDate endAt = LocalDate.of(2024, 10, 18);
    concertHallEntity = ConcertHallEntity.of("KSPO DOME", "서울특별시 송파구 올림픽로 424 (방이동 88-2) 올림픽공원", "02-410-1114", null);
    concert = ConcertEntity.of("박효신 콘서트", concertHallEntity.getId(), "ballad", 120, ConcertAgeRestriction.OVER_15, startAt, endAt);

    seatNumber1 = ConcertHallSeatEntity.of(concertHallEntity.getId(), 1L);
    seatNumber100 = ConcertHallSeatEntity.of(concertHallEntity.getId(), 100L);

    ConcertSeatGradeEntity allGrade = ConcertSeatGradeEntity.of(concert.getId(), SeatGrade.ALL, 100000);
    ConcertSeatGradeEntity standingGrade = ConcertSeatGradeEntity.of(concert.getId(), SeatGrade.STANDING, 80000);

    LocalDateTime firstDateTime = LocalDateTime.of(2024, 10, 16, 22, 30);
    firstSchedule = ConcertScheduleEntity.of(concert.getId(), firstDateTime);

    firstALLConcertScheduleSeat = ConcertScheduleSeatEntity.of(seatNumber1.getId(), firstSchedule.getId(), allGrade.getId(), ConcertScheduleSeatStatus.AVAILABLE);
    firstSTANDINGConcertScheduleSeat = ConcertScheduleSeatEntity.of(seatNumber100.getId(), firstSchedule.getId(), standingGrade.getId(), ConcertScheduleSeatStatus.AVAILABLE);

    LocalDateTime secondDateTime = LocalDateTime.of(2024, 10, 18, 22, 30);
    secondSchedule = ConcertScheduleEntity.of(concert.getId(), secondDateTime);

    secondALLConcertScheduleSeat = ConcertScheduleSeatEntity.of(seatNumber1.getId(), secondSchedule.getId(), allGrade.getId(), ConcertScheduleSeatStatus.AVAILABLE);
    secondSTANDINGConcertScheduleSeat = ConcertScheduleSeatEntity.of(seatNumber100.getId(), secondSchedule.getId(), allGrade.getId(), ConcertScheduleSeatStatus.AVAILABLE);
  }

  @Nested
  @DisplayName("예약 가능한 공연 날짜를 찾을 때")
  class 예약_가능한_공연_날짜를_찾을_때 {

    @Test
    @DisplayName("두 번의 공연 날짜에 대해서, 모두 예약 가능하다")
    void 두_번의_공연_날짜에_대해서_모두_예약_가능하다() {
      // given
      given(concertService.getConcertById(concertId))
              .willReturn(concert);

      given(concertScheduleService.getActiveConcertSchedules(concertId))
              .willReturn(List.of(firstSchedule.getDateTime(), secondSchedule.getDateTime()));

      // when
      List<LocalDateTime> availableDateTimes = sut.getActiveConcertSchedules(concertId);

      // then
      assertEquals(2, availableDateTimes.size());
    }

    @Test
    @DisplayName("두 번의 공연 날짜에 대해서, 모두 예약이 불가능하다")
    void 두_번의_공연_날짜에_대해서_모두_예약이_불가능하다() {
      // given
      given(concertService.getConcertById(concertId))
              .willReturn(concert);

      given(concertScheduleService.getActiveConcertSchedules(concertId))
              .willReturn(List.of());

      // when
      List<LocalDateTime> availableDateTimes = sut.getActiveConcertSchedules(concertId);

      // then
      assertEquals(0, availableDateTimes.size());
    }
  }
}
