package com.example.concert.reservation.repository;

import com.example.concert.reservation.domain.Reservation;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM Reservation r WHERE r.concertSchedule.id = :concertScheduleId AND r.seat.id = :seatId")
    Optional<Reservation> findReservationByConcertScheduleIdAndSeatId(@Param(value="concertScheduleId") long concertScheduleId, @Param(value="seatId") long seatId);
}
