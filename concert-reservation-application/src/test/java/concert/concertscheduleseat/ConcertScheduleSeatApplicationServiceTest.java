package concert.concertscheduleseat;

import concert.application.concert.business.ConcertScheduleSeatApplicationService;
import concert.domain.concert.entities.ConcertEntity;
import concert.domain.concert.entities.ConcertScheduleEntity;
import concert.domain.concert.entities.ConcertScheduleSeatEntity;
import concert.domain.concert.entities.ConcertSeatGradeEntity;
import concert.domain.concert.entities.enums.ConcertAgeRestriction;
import concert.domain.concert.entities.enums.ConcertScheduleSeatStatus;
import concert.domain.concert.entities.enums.SeatGrade;
import concert.domain.concert.services.ConcertScheduleService;
import concert.domain.concerthall.entities.ConcertHallEntity;
import concert.domain.concerthall.entities.ConcertHallSeatEntity;
import concert.domain.member.entities.MemberEntity;
import concert.domain.member.services.MemberService;
import concert.domain.concert.services.ConcertScheduleSeatService;
import concert.domain.shared.exceptions.CustomException;
import concert.domain.shared.utils.TimeProvider;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ConcertScheduleSeatApplicationServiceTest {
  @Mock
  private TimeProvider timeProvider;
  @Mock
  private MemberService memberService;
  @Mock
  private ConcertScheduleService concertScheduleService;
  @Mock
  private ConcertScheduleSeatService concertScheduleSeatService;
  @InjectMocks
  private ConcertScheduleSeatApplicationService sut;

  @Nested
  @DisplayName("좌석 예약을 할 때")
  class 좌석_예약을_할_때 {

    @Test
    @DisplayName("모든 유효성 검사를 통과하고, 좌석 예약이 6분전에 일어났으면 좌석 예약이 가능하다")
    @Disabled
    void 모든_유효성_검사를_통과하고_좌석_예약이_6분전에_일어났으면_좌석_예약이_가능하다() {

      MemberEntity member = MemberEntity.of("Tom Cruise");
      String uuid = member.getUuid().toString();

      long concertScheduleId = 1L;
      LocalDate startAt = LocalDate.of(2024, 10, 16);
      LocalDate endAt = LocalDate.of(2024, 10, 18);

      ConcertHallEntity concertHallEntity = ConcertHallEntity.of("KSPO DOME", "서울특별시 송파구 올림픽로 424 (방이동 88-2) 올림픽공원", "02-410-1114", null);
      ConcertEntity concert = ConcertEntity.of("박효신 콘서트", concertHallEntity.getId(), "ballad", 120, ConcertAgeRestriction.OVER_15, startAt, endAt);

      LocalDateTime dateTime = LocalDateTime.of(2024, 10, 16, 22, 30);
      ConcertScheduleEntity concertSchedule = ConcertScheduleEntity.of(concert.getId(), dateTime);

      long number = 1L;
      ConcertHallSeatEntity seat = ConcertHallSeatEntity.of(concertHallEntity.getId(), 1);
      ConcertSeatGradeEntity seatGrade = ConcertSeatGradeEntity.of(concert.getId(), SeatGrade.ALL, 100000);
      ConcertScheduleSeatEntity concertScheduleSeat = ConcertScheduleSeatEntity.of(seat.getId(), concertSchedule.getId(), seatGrade.getId(), ConcertScheduleSeatStatus.AVAILABLE);

      given(memberService.getMemberByUuid(uuid)).willReturn(member);
      given(concertScheduleService.getConcertScheduleById(concertScheduleId)).willReturn(concertSchedule);
      given(concertScheduleSeatService.getConcertScheduleSeat(concertScheduleSeat.getId())).willReturn(concertScheduleSeat);
      given(timeProvider.now()).willReturn(LocalDateTime.of(2024, 10, 18, 0, 0));
      seat.setUpdatedAt(timeProvider.now().minusMinutes(6));

      String token = "12345";

      sut.reserveConcertScheduleSeats(token, List.of(concertScheduleSeat.getId()));
    }

    @Test
    @Disabled
    @DisplayName("좌석_예약이_4분전에_일어났으면_좌석_예약이_불가능하다")
    void 좌석_예약이_4분전에_일어났으면_좌석_예약이_불가능하다() {

      MemberEntity member = MemberEntity.of("Tom Cruise");
      String uuid = member.getUuid().toString();

      long concertScheduleId = 1L;
      LocalDate startAt = LocalDate.of(2024, 10, 16);
      LocalDate endAt = LocalDate.of(2024, 10, 18);

      ConcertHallEntity concertHallEntity = ConcertHallEntity.of("KSPO DOME", "서울특별시 송파구 올림픽로 424 (방이동 88-2) 올림픽공원", "02-410-1114", null);
      ConcertEntity concert = ConcertEntity.of("박효신 콘서트", concertHallEntity.getId(), "ballad", 120, ConcertAgeRestriction.OVER_15, startAt, endAt);

      LocalDateTime dateTime = LocalDateTime.of(2024, 10, 16, 22, 30);
      ConcertScheduleEntity concertSchedule = ConcertScheduleEntity.of(concert.getId(), dateTime);

      long number = 1L;
      ConcertHallSeatEntity seat = ConcertHallSeatEntity.of(concertHallEntity.getId(), 1);
      ConcertSeatGradeEntity seatGrade = ConcertSeatGradeEntity.of(concert.getId(), SeatGrade.ALL, 100000);
      ConcertScheduleSeatEntity concertScheduleSeat = ConcertScheduleSeatEntity.of(seat.getId(), concertSchedule.getId(), seatGrade.getId(), ConcertScheduleSeatStatus.AVAILABLE);

      given(memberService.getMemberByUuid(uuid)).willReturn(member);
      given(concertScheduleService.getConcertScheduleById(concertScheduleId)).willReturn(concertSchedule);
      given(concertScheduleSeatService.getConcertScheduleSeat(concertScheduleSeat.getId())).willReturn(concertScheduleSeat);
      given(timeProvider.now()).willReturn(LocalDateTime.of(2024, 10, 18, 0, 0));
      seat.setUpdatedAt(timeProvider.now().minusMinutes(4));

      String token = "12345";

      assertThrows(NullPointerException.class, () -> sut.reserveConcertScheduleSeats(token, List.of(concertScheduleSeat.getId())));
    }
  }
}
