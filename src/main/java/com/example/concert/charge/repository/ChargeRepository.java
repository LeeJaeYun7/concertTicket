package com.example.concert.charge.repository;

import com.example.concert.charge.domain.Charge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChargeRepository extends JpaRepository<Charge, Long> {
}
