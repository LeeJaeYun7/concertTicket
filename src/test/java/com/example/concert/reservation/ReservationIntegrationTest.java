package com.example.concert.reservation;

import com.example.concert.concert.domain.Concert;
import com.example.concert.concert.repository.ConcertRepository;
import com.example.concert.concertschedule.domain.ConcertSchedule;
import com.example.concert.concertschedule.repository.ConcertScheduleRepository;
import com.example.concert.reservation.domain.Reservation;
import com.example.concert.reservation.repository.ReservationRepository;
import com.example.concert.reservation.service.ReservationService;
import com.example.concert.seat.domain.Seat;
import com.example.concert.seat.domain.SeatStatus;
import com.example.concert.seat.repository.SeatRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class ReservationIntegrationTest {

    @Autowired
    private ReservationService sut;
    @Autowired
    private ConcertRepository concertRepository;
    @Autowired
    private ConcertScheduleRepository concertScheduleRepository;
    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Nested
    @DisplayName("예약을 생성할 때")
    class 예약을_생성할때 {
        @Test
        @DisplayName("ConcertSchedule, uuid, Seat, price가 전달될 때, 예약이 생성된다")
        void ConcertSchedule_uuid_Seat_price가_전달될때_예약이_생성된다() {
            Concert concert = Concert.of("브루노 마스 콘서트");
            LocalDateTime dateTime = LocalDateTime.of(2024, 10, 16, 22, 30);
            ConcertSchedule concertSchedule = ConcertSchedule.of(concert, dateTime, 50000);

            concertRepository.save(concert);
            concertScheduleRepository.save(concertSchedule);

            UUID uuid = UUID.randomUUID();
            Seat seat = Seat.of(concertSchedule, 1, 50000, SeatStatus.AVAILABLE);
            seatRepository.save(seat);

            Reservation reservation = Reservation.of(concertSchedule, uuid, seat, 50000);
            reservationRepository.save(reservation);

            Reservation savedReservation = sut.createReservation(concertSchedule, uuid, seat, 50000);

            assertEquals("브루노 마스 콘서트", savedReservation.getConcertSchedule().getConcert().getName());
            assertEquals(50000, savedReservation.getConcertSchedule().getPrice());
            assertEquals(1, savedReservation.getSeat().getNumber());
        }
    }
}
