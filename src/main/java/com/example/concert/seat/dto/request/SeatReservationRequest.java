package com.example.concert.seat.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
public class SeatReservationRequest {

    private final UUID uuid;
    private final long concertScheduleId;
    private final long number;

    @Builder
    public SeatReservationRequest(UUID uuid, long concertScheduleId, long number){
        this.uuid = uuid;
        this.concertScheduleId = concertScheduleId;
        this.number = number;
    }
}
