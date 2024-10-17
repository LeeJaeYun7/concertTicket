package com.example.concert.seat;

import com.example.concert.concert.domain.Concert;
import com.example.concert.concertschedule.domain.ConcertSchedule;
import com.example.concert.seat.domain.Seat;
import com.example.concert.seat.domain.SeatStatus;
import com.example.concert.seat.repository.SeatRepository;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SeatServiceTest {

    @Mock
    private TimeProvider timeProvider;

    @Mock
    private SeatRepository seatRepository;

    @InjectMocks
    private SeatService sut;

    @Nested
    @DisplayName("예약 가능한 좌석을 조회할 때")
    class 예약_가능한_좌석을_조회할때 {

        @Test
        @DisplayName("concertScheduleId가 전달될 때, 예약 가능한 좌석이 조회된다")
        void concertScheduleId가_전달될때_예약_가능한_좌석이_조회된다() {
            Concert concert = Concert.of("박효신 콘서트");
            LocalDateTime dateTime = LocalDateTime.of(2024, 10, 16, 22, 30);
            ConcertSchedule concertSchedule = ConcertSchedule.of(concert, dateTime, 50000);

            when(timeProvider.now()).thenReturn(LocalDateTime.of(2024, 10, 18, 0, 0));
            LocalDateTime threshold = timeProvider.now().minusMinutes(5);

            Seat seat1 = Seat.of(concertSchedule, 1, 50000, SeatStatus.AVAILABLE);
            Seat seat2 = Seat.of(concertSchedule, 2, 50000, SeatStatus.AVAILABLE);
            List<Seat> availableSeats = List.of(seat1, seat2);

            given(seatRepository.findAllAvailableSeatsByConcertScheduleIdAndStatus(1L, SeatStatus.AVAILABLE, threshold))
                    .willReturn(availableSeats);

            List<Seat> result = sut.getAllAvailableSeats(1L);
            assertEquals(result.get(0).getNumber(), availableSeats.get(0).getNumber());
            assertEquals(result.get(1).getNumber(), availableSeats.get(1).getNumber());
        }
    }

    @Nested
    @DisplayName("좌석의 업데이트 시각을 최신화할 때")
    class 업데이트_시각을_최신화할때 {

        @Test
        @DisplayName("업데이트 시각을 최신화할 때 성공한다")
        void 업데이트_시각을_최신화할때_성공한다() throws Exception {
            Concert concert = Concert.of("박효신 콘서트");
            LocalDateTime dateTime = LocalDateTime.of(2024, 10, 16, 22, 30);
            ConcertSchedule concertSchedule = ConcertSchedule.of(concert, dateTime, 50000);

            Seat seat = Seat.of(concertSchedule, 1, 50000, SeatStatus.AVAILABLE);
            when(seatRepository.findByConcertScheduleIdAndNumberWithLock(1L, 1))
                    .thenReturn(Optional.of(seat));
            when(timeProvider.now()).thenReturn(LocalDateTime.of(2024, 10, 18, 0, 0));

            sut.changeUpdatedAt(1L, 1);

            assertEquals(seat.getUpdatedAt(), LocalDateTime.of(2024, 10, 18, 0, 0));
        }
    }

    @Nested
    @DisplayName("좌석 상태를 업데이트할 때")
    class 좌석_상태를_업데이트할때 {
        @Test
        @DisplayName("좌석 상태를 업데이트할 때 성공한다")
        void 좌석_상태를_업데이트할_때_성공한다() throws Exception {
            Concert concert = Concert.of("박효신 콘서트");
            LocalDateTime dateTime = LocalDateTime.of(2024, 10, 16, 22, 30);
            ConcertSchedule concertSchedule = ConcertSchedule.of(concert, dateTime, 50000);

            Seat seat = Seat.of(concertSchedule, 1, 50000, SeatStatus.AVAILABLE);
            when(seatRepository.findByConcertScheduleIdAndNumberWithLock(1L, 1))
                    .thenReturn(Optional.of(seat));

            sut.updateSeatStatus(1L, 1, SeatStatus.RESERVED);

            assertEquals(seat.getStatus(), SeatStatus.RESERVED);
        }
    }
}