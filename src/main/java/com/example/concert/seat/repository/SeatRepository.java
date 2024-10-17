package com.example.concert.seat.repository;

import com.example.concert.seat.domain.Seat;
import com.example.concert.seat.domain.SeatStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    @Query("SELECT s FROM Seat s WHERE s.concertSchedule.id = :concertScheduleId AND s.status = :status AND s.updatedAt <= :threshold")
    List<Seat> findAllAvailableSeatsByConcertScheduleIdAndStatus(@Param("concertScheduleId") long concertScheduleId, @Param("status") SeatStatus status, @Param("threshold") LocalDateTime threshold);

    Optional<Seat> findByConcertScheduleIdAndNumber(long concertScheduleId, long number);

    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("SELECT s FROM Seat s WHERE s.concertSchedule.id = :concertScheduleId AND s.number = :number")
    Optional<Seat> findByConcertScheduleIdAndNumberWithLock(@Param("concertScheduleId") long concertScheduleId, @Param("number") long number);
}
