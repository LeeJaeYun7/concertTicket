package com.example.concert.waitingQueue.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class TokenRequest {

    private long concertId;
    private UUID uuid;

    @Builder
    public TokenRequest(long concertId, UUID uuid){
        this.concertId = concertId;
        this.uuid = uuid;
    }
}
