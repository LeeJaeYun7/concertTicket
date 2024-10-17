package com.example.concert.concertschedule.dto.request;

import lombok.Getter;

@Getter
public class SeatNumbersRequest {

    private final String token;
    private final long concertScheduleId;

    public SeatNumbersRequest(String token, long concertScheduleId){
        this.token = token;
        this.concertScheduleId = concertScheduleId;
    }

}
