package com.example.concert.concertschedule.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ConcertScheduleRequest {

    private String token;
    private long concertId;

    @Builder
    public ConcertScheduleRequest(String token, long concertId){
        this.token = token;
        this.concertId = concertId;
    }
}
