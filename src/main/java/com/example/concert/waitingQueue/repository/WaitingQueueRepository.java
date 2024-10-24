package com.example.concert.waitingQueue.repository;

import com.example.concert.waitingQueue.domain.WaitingQueue;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WaitingQueueRepository extends JpaRepository<WaitingQueue, Long> {
    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("SELECT w FROM WaitingQueue w WHERE w.concert.id = :concertId AND w.waitingNumber > 0")
    List<WaitingQueue> findAllByConcertIdWithLock(@Param("concertId") long concertId);

    Optional<WaitingQueue> findByConcert_IdAndUuid(long concertId, String uuid);
    Optional<WaitingQueue> findByUuid(String uuid);

    void deleteByConcert_IdAndUuid(long concertId, String uuid);

    Optional<WaitingQueue> findByToken(String token);

    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("SELECT w FROM WaitingQueue w WHERE w.concert.id = :concertId AND w.waitingNumber = :waitingNumber")
    Optional<WaitingQueue> findByConcertIdAndWaitingNumber(@Param("concertId") long concertId, @Param("waitingNumber") long waitingNumber);
}
