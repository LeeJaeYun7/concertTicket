package com.example.concert.waitingQueue.vo;

import lombok.Builder;
import lombok.Getter;

@Getter
public class TokenVO {

    private final String newToken;
    private final long waitingNumber;

    @Builder
    public TokenVO(String newToken, long waitingNumber){
        this.newToken = newToken;
        this.waitingNumber = waitingNumber;
    }

    public static TokenVO of(String newToken, long waitingNumber){
        return TokenVO.builder()
                      .newToken(newToken)
                      .waitingNumber(waitingNumber)
                      .build();
    }
}
