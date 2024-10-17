package com.example.concert.reservation.service;

import com.example.concert.concertschedule.domain.ConcertSchedule;
import com.example.concert.reservation.domain.Reservation;
import com.example.concert.reservation.repository.ReservationRepository;
import com.example.concert.seat.domain.Seat;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository){
        this.reservationRepository = reservationRepository;
    }

    public void createReservation(ConcertSchedule concertSchedule, UUID uuid, Seat seat, long price) {
        Reservation reservation = Reservation.of(concertSchedule, uuid, seat, price);
        reservationRepository.save(reservation);
    }
}
