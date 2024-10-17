package com.example.concert.charge.service;

import com.example.concert.charge.domain.Charge;
import com.example.concert.charge.repository.ChargeRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ChargeService {

    private final ChargeRepository chargeRepository;

    public ChargeService(ChargeRepository chargeRepository){
        this.chargeRepository = chargeRepository;
    }

    public void createCharge(UUID uuid, long amount){
        Charge charge = Charge.of(uuid, amount);
        chargeRepository.save(charge);
    }
}
