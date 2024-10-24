package com.example.concert.charge.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class ChargeRequest {

    private String uuid;
    private long amount;

    public ChargeRequest(String uuid, long amount){
        this.uuid = uuid;
        this.amount = amount;
    }
}
