package com.example.concert.charge.dto.response;

import lombok.Builder;

public class ChargeResponse {

    private final long updatedBalance;

    @Builder
    public ChargeResponse(long updatedBalance){
        this.updatedBalance = updatedBalance;
    }

    public static ChargeResponse of(long updatedBalance){
        return ChargeResponse.builder()
                             .updatedBalance(updatedBalance)
                             .build();
    }
}
