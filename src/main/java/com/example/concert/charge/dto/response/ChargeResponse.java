package com.example.concert.charge.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChargeResponse {

    private long updatedBalance;

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
