package com.example.concert.reservation.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class ReservationRequest {

    private String uuid;
    private long concertScheduleId;
    private long seatNumber;

    public ReservationRequest(String uuid, long concertScheduleId, long seatNumber){
        this.uuid = uuid;
        this.concertScheduleId = concertScheduleId;
        this.seatNumber = seatNumber;
    }
}
