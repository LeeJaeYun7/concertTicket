package com.example.concert.reservation.dto;

import lombok.Getter;

import java.util.UUID;

@Getter
public class ReservationRequest {

    private final String token;
    private final UUID uuid;
    private final long concertScheduleId;
    private final long seatNumber;

    public ReservationRequest(String token, UUID uuid, long concertScheduleId, long seatNumber){
        this.token = token;
        this.uuid = uuid;
        this.concertScheduleId = concertScheduleId;
        this.seatNumber = seatNumber;
    }
}
