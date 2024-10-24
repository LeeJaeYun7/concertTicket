package com.example.concert.seat;

import com.example.concert.common.CustomException;
import com.example.concert.concert.domain.Concert;
import com.example.concert.concertschedule.domain.ConcertSchedule;
import com.example.concert.concertschedule.service.ConcertScheduleService;
import com.example.concert.member.domain.Member;
import com.example.concert.member.service.MemberService;
import com.example.concert.seat.domain.Seat;
import com.example.concert.seat.domain.SeatStatus;
import com.example.concert.seat.service.SeatFacade;
import com.example.concert.seat.service.SeatService;
import com.example.concert.utils.TimeProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class SeatFacadeTest {
    @Mock
    private TimeProvider timeProvider;
    @Mock
    private MemberService memberService;
    @Mock
    private ConcertScheduleService concertScheduleService;
    @Mock
    private SeatService seatService;
    @InjectMocks
    private SeatFacade sut;

    @Nested
    @DisplayName("좌석 예약을 할 때")
    class 좌석_예약을_할_때 {

        @Test
        @DisplayName("모든 유효성 검사를 통과하고, 좌석 예약이 6분전에 일어났으면 좌석 예약이 가능하다")
        void 모든_유효성_검사를_통과하고_좌석_예약이_6분전에_일어났으면_좌석_예약이_가능하다() {

            Member member = Member.of("Tom Cruise");
            String uuid = member.getUuid().toString();

            long concertScheduleId = 1L;
            Concert concert = Concert.of("박효신 콘서트");
            LocalDateTime dateTime = LocalDateTime.of(2024, 10, 16, 22, 30);
            ConcertSchedule concertSchedule = ConcertSchedule.of(concert, dateTime, 50000);

            long number = 1L;
            Seat seat = Seat.of(concertSchedule, 1, 50000, SeatStatus.AVAILABLE);

            given(memberService.getMemberByUuid(uuid)).willReturn(member);
            given(concertScheduleService.getConcertScheduleById(concertScheduleId)).willReturn(concertSchedule);
            given(seatService.getSeatByConcertScheduleIdAndNumberWithLock(concertScheduleId, number)).willReturn(seat);
            given(timeProvider.now()).willReturn(LocalDateTime.of(2024, 10, 18, 0, 0));
            seat.setUpdatedAt(timeProvider.now().minusMinutes(6));

            sut.createSeatReservation(uuid, concertScheduleId, number);
        }

        @Test
        @DisplayName("좌석_예약이_4분전에_일어났으면_좌석_예약이_불가능하다")
        void 좌석_예약이_4분전에_일어났으면_좌석_예약이_불가능하다() {

            Member member = Member.of("Tom Cruise");
            String uuid = member.getUuid().toString();

            long concertScheduleId = 1L;
            Concert concert = Concert.of("박효신 콘서트");
            LocalDateTime dateTime = LocalDateTime.of(2024, 10, 16, 22, 30);
            ConcertSchedule concertSchedule = ConcertSchedule.of(concert, dateTime, 50000);

            long number = 1L;
            Seat seat = Seat.of(concertSchedule, 1, 50000, SeatStatus.AVAILABLE);

            given(memberService.getMemberByUuid(uuid)).willReturn(member);
            given(concertScheduleService.getConcertScheduleById(concertScheduleId)).willReturn(concertSchedule);
            given(seatService.getSeatByConcertScheduleIdAndNumberWithLock(concertScheduleId, number)).willReturn(seat);
            given(timeProvider.now()).willReturn(LocalDateTime.of(2024, 10, 18, 0, 0));
            seat.setUpdatedAt(timeProvider.now().minusMinutes(4));

            assertThrows(CustomException.class, () -> sut.createSeatReservation(uuid, concertScheduleId, number));
        }
    }
}
