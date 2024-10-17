package com.example.concert.waitingQueue.dto.response;

import lombok.Builder;
import lombok.Getter;
@Getter
public class TokenResponse {

    private final String token;
    @Builder
    public TokenResponse(String token){
        this.token = token;
    }

    public static TokenResponse of(String token){
        return TokenResponse.builder()
                            .token(token)
                            .build();
    }
}
