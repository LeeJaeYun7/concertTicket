package com.example.concert.charge.dto.request;

import lombok.Getter;

import java.util.UUID;

@Getter
public class ChargeRequest {

    private final UUID uuid;
    private final long amount;

    public ChargeRequest(UUID uuid, long amount){
        this.uuid = uuid;
        this.amount = amount;
    }
}
