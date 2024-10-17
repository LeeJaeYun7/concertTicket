package com.example.concert.waitingQueue.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class WaitingNumberResponse {

    private final long waitingNumber;

    @Builder
    public WaitingNumberResponse(long waitingNumber){
        this.waitingNumber = waitingNumber;
    }

    public static WaitingNumberResponse of(long waitingNumber){
        return WaitingNumberResponse.builder()
                            .waitingNumber(waitingNumber)
                            .build();
    }
}
