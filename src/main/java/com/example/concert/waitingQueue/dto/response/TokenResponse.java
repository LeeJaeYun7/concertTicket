package com.example.concert.waitingQueue.dto.response;

import lombok.Builder;
import lombok.Getter;
@Getter
public class TokenResponse {

    private final String token;
    private final long waitingNumber;
    @Builder
    public TokenResponse(String token, long waitingNumber){
        this.token = token;
        this.waitingNumber = waitingNumber;
    }

    public static TokenResponse of(String token, long waitingNumber){
        return TokenResponse.builder()
                            .token(token)
                            .waitingNumber(waitingNumber)
                            .build();
    }
}
