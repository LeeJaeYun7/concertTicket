package com.example.concert.concertschedule.repository;

import com.example.concert.concertschedule.domain.ConcertSchedule;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ConcertScheduleRepository extends JpaRepository<ConcertSchedule, Long> {
    @Query("SELECT c FROM ConcertSchedule c WHERE c.concert.id = :concertId AND c.dateTime >= :now")
    List<ConcertSchedule> findAllAfterNowByConcertId(@Param("concertId") long concertId, @Param("now") LocalDateTime now);

    @Query("SELECT c.name, cs.dateTime FROM ConcertSchedule cs INNER JOIN Concert c ON cs.concert.id = c.id WHERE cs.dateTime >= :now")
    List<Tuple> findAllAfterNow(@Param("now") LocalDateTime now);
}
