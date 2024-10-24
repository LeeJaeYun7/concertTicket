package com.example.concert.reservation.dto.request;

import lombok.Getter;

import java.util.UUID;

@Getter
public class ReservationRequest {

    private final UUID uuid;
    private final long concertScheduleId;
    private final long seatNumber;

    public ReservationRequest(UUID uuid, long concertScheduleId, long seatNumber){
        this.uuid = uuid;
        this.concertScheduleId = concertScheduleId;
        this.seatNumber = seatNumber;
    }
}
