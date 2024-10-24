package com.example.concert.charge.repository;

import com.example.concert.charge.domain.Charge;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChargeRepository extends JpaRepository<Charge, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Charge c WHERE c.uuid = :uuid")
    Optional<Charge> findChargeByUuid(@Param(value="uuid") String uuid);

    Charge save(Charge charge);
}
