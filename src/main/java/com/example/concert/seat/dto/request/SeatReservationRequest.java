package com.example.concert.seat.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
public class SeatReservationRequest {

    private final String token;
    private final UUID uuid;
    private final long concertScheduleId;
    private final long number;

    @Builder
    public SeatReservationRequest(String token, UUID uuid, long concertScheduleId, long number){
        this.token = token;
        this.uuid = uuid;
        this.concertScheduleId = concertScheduleId;
        this.number = number;
    }
}
