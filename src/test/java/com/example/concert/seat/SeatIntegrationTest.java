package com.example.concert.seat;

import com.example.concert.concert.domain.Concert;
import com.example.concert.concert.repository.ConcertRepository;
import com.example.concert.concertschedule.domain.ConcertSchedule;
import com.example.concert.concertschedule.repository.ConcertScheduleRepository;
import com.example.concert.seat.domain.Seat;
import com.example.concert.seat.domain.SeatStatus;
import com.example.concert.seat.repository.SeatRepository;
import com.example.concert.seat.service.SeatService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
@SpringBootTest
@Transactional
public class SeatIntegrationTest {

    @Autowired
    private SeatService sut;

    @Autowired
    private ConcertRepository concertRepository;

    @Autowired
    private ConcertScheduleRepository concertScheduleRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Nested
    @DisplayName("예약 가능한 좌석을 조회할 때")
    class 예약_가능한_좌석을_조회할때 {

        @Test
        @DisplayName("concertScheduleId가 전달될 때, 예약 가능한 좌석이 조회된다")
        void concertScheduleId가_전달될때_예약_가능한_좌석이_조회된다() {
            Concert concert = Concert.of("박효신 콘서트");
            LocalDateTime dateTime = LocalDateTime.of(2024, 10, 16, 22, 30);
            concertRepository.save(concert);

            ConcertSchedule concertSchedule = ConcertSchedule.of(concert, dateTime, 50000);
            ConcertSchedule savedConcertSchedule = concertScheduleRepository.save(concertSchedule);

            Seat seat1 = Seat.of(concertSchedule, 11, 50000, SeatStatus.AVAILABLE);
            seat1.setUpdatedAt(LocalDateTime.now().minusMinutes(10));
            Seat seat2 = Seat.of(concertSchedule, 22, 50000, SeatStatus.AVAILABLE);
            seat2.setUpdatedAt(LocalDateTime.now().minusMinutes(10));

            seatRepository.save(seat1);
            seatRepository.save(seat2);

            List<Seat> result = sut.getAllAvailableSeats(savedConcertSchedule.getId());
            assertEquals(result.get(0).getNumber(), seat1.getNumber());
            assertEquals(result.get(1).getNumber(), seat2.getNumber());
        }
    }

    @Nested
    @DisplayName("좌석의 업데이트 시각을 최신화할 때")
    class 업데이트_시각을_최신화할때 {

        @Test
        @DisplayName("업데이트 시각을 최신화할 때 성공한다")
        void 업데이트_시각을_최신화할때_성공한다() {
            Concert concert = Concert.of("박효신 콘서트");
            concertRepository.save(concert);

            LocalDateTime dateTime = LocalDateTime.of(2024, 10, 16, 22, 30);
            ConcertSchedule concertSchedule = ConcertSchedule.of(concert, dateTime, 50000);
            ConcertSchedule savedConcertSchedule = concertScheduleRepository.save(concertSchedule);

            Seat seat = Seat.of(concertSchedule, 1, 50000, SeatStatus.AVAILABLE);
            seatRepository.save(seat);

            sut.changeUpdatedAt(savedConcertSchedule.getId(), 1);

            assertEquals(seat.getUpdatedAt(), LocalDateTime.now());
        }
    }

    @Nested
    @DisplayName("좌석 상태를 업데이트할 때")
    class 좌석_상태를_업데이트할때 {
        @Test
        @DisplayName("좌석 상태를 업데이트할 때 성공한다")
        void 좌석_상태를_업데이트할_때_성공한다() {
            Concert concert = Concert.of("박효신 콘서트");
            concertRepository.save(concert);

            LocalDateTime dateTime = LocalDateTime.of(2024, 10, 16, 22, 30);
            ConcertSchedule concertSchedule = ConcertSchedule.of(concert, dateTime, 50000);
            ConcertSchedule savedConcertSchedule = concertScheduleRepository.save(concertSchedule);

            Seat seat = Seat.of(concertSchedule, 11, 50000, SeatStatus.AVAILABLE);
            seatRepository.save(seat);

            sut.updateSeatStatus(savedConcertSchedule.getId(), 11, SeatStatus.RESERVED);

            assertEquals(seat.getStatus(), SeatStatus.RESERVED);
        }
    }
}
