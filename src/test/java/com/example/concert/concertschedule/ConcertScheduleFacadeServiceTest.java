package com.example.concert.concertschedule;

import com.example.concert.concert.domain.Concert;
import com.example.concert.concertschedule.domain.ConcertSchedule;
import com.example.concert.concertschedule.dto.response.ConcertScheduleResponse;
import com.example.concert.concertschedule.service.ConcertScheduleFacadeService;
import com.example.concert.concertschedule.service.ConcertScheduleService;
import com.example.concert.seat.domain.Seat;
import com.example.concert.seat.domain.SeatStatus;
import com.example.concert.seat.service.SeatService;
import com.example.concert.utils.RandomStringGenerator;
import com.example.concert.utils.TokenValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ConcertScheduleFacadeServiceTest {

    @Mock
    private TokenValidator tokenValidator;

    @Mock
    private ConcertScheduleService concertScheduleService;

    @Mock
    private SeatService seatService;

    @InjectMocks
    private ConcertScheduleFacadeService sut;


    @Nested
    @DisplayName("예약 가능한 공연 날짜를 찾을 때")
    class 예약_가능한_공연_날짜를_찾을_때 {

        @Test
        @DisplayName("두 번의 공연 날짜에 대해서, 모두 예약 가능하다")
        void 두_번의_공연_날짜에_대해서_모두_예약_가능하다() throws Exception {
            long concertId = 1L;
            Concert concert1 = Concert.of("박효신 콘서트");
            LocalDateTime dateTime1 = LocalDateTime.of(2024, 10, 16, 22, 30);
            ConcertSchedule concertSchedule1 = ConcertSchedule.of(concert1, dateTime1, 50000);
            Seat seat1 = Seat.of(concertSchedule1, 1, 50000, SeatStatus.AVAILABLE);
            Seat seat2 = Seat.of(concertSchedule1, 2, 50000, SeatStatus.AVAILABLE);

            LocalDateTime dateTime2 = LocalDateTime.of(2024, 10, 18, 22, 30);
            ConcertSchedule concertSchedule2 = ConcertSchedule.of(concert1, dateTime2, 50000);
            Seat seat3 = Seat.of(concertSchedule2, 1, 50000, SeatStatus.AVAILABLE);
            Seat seat4 = Seat.of(concertSchedule2, 2, 50000, SeatStatus.AVAILABLE);

            String token = RandomStringGenerator.generateRandomString(16);
            given(tokenValidator.validateToken(token)).willReturn(true);
            given(concertScheduleService.getAllConcertSchedulesAfterNowByConcertId(concertId)).willReturn(List.of(concertSchedule1, concertSchedule2));
            given(seatService.getAllAvailableSeats(concertSchedule1.getId())).willReturn(List.of(seat1, seat2));
            given(seatService.getAllAvailableSeats(concertSchedule2.getId())).willReturn(List.of(seat3, seat4));

            ConcertScheduleResponse concertScheduleResponse = sut.getAvailableDateTimes(token, 1L);
            assertEquals(2, concertScheduleResponse.getAvailableDateTimes().size());
        }

        @Test
        @DisplayName("두 번의 공연 날짜에_대해서 모두 예약이 불가능하다")
        void 두_번의_공연_날짜에_대해서_모두_예약이_불가능하다() throws Exception {
            long concertId = 1L;
            Concert concert1 = Concert.of("박효신 콘서트");
            LocalDateTime dateTime1 = LocalDateTime.of(2024, 10, 16, 22, 30);
            ConcertSchedule concertSchedule1 = ConcertSchedule.of(concert1, dateTime1, 50000);

            LocalDateTime dateTime2 = LocalDateTime.of(2024, 10, 18, 22, 30);
            ConcertSchedule concertSchedule2 = ConcertSchedule.of(concert1, dateTime2, 50000);

            String token = RandomStringGenerator.generateRandomString(16);
            given(tokenValidator.validateToken(token)).willReturn(true);
            given(concertScheduleService.getAllConcertSchedulesAfterNowByConcertId(concertId)).willReturn(List.of(concertSchedule1, concertSchedule2));
            given(seatService.getAllAvailableSeats(concertSchedule1.getId())).willReturn(List.of());
            given(seatService.getAllAvailableSeats(concertSchedule2.getId())).willReturn(List.of());

            ConcertScheduleResponse concertScheduleResponse = sut.getAvailableDateTimes(token, 1L);
            assertEquals(0, concertScheduleResponse.getAvailableDateTimes().size());
        }
    }
}
