package com.example.concert.seat.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class SeatReservationRequest {

    private String uuid;
    private long concertScheduleId;
    private long number;

    @Builder
    public SeatReservationRequest(String uuid, long concertScheduleId, long number){
        this.uuid = uuid;
        this.concertScheduleId = concertScheduleId;
        this.number = number;
    }
}
