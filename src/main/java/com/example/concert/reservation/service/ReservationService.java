package com.example.concert.reservation.service;

import com.example.concert.concertschedule.domain.ConcertSchedule;
import com.example.concert.reservation.domain.Reservation;
import com.example.concert.reservation.repository.ReservationRepository;
import com.example.concert.seat.domain.Seat;
import org.springframework.stereotype.Service;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository){
        this.reservationRepository = reservationRepository;
    }

    public Reservation createReservation(ConcertSchedule concertSchedule, String uuid, Seat seat, long price) {
        reservationRepository.findReservationByConcertScheduleIdAndSeatId(concertSchedule.getId(), seat.getId());
        Reservation reservation = Reservation.of(concertSchedule, uuid, seat, price);
        return reservationRepository.save(reservation);
    }
}
