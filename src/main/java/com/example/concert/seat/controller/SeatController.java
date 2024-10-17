package com.example.concert.seat.controller;

import com.example.concert.seat.dto.request.SeatReservationRequest;
import com.example.concert.seat.service.SeatFacadeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class SeatController {

    private final SeatFacadeService seatFacadeService;

    public SeatController(SeatFacadeService seatFacadeService){
        this.seatFacadeService = seatFacadeService;
    }

    @PostMapping("/seat/reservation")
    public ResponseEntity<Void> createSeatReservation(@RequestBody SeatReservationRequest seatReservationRequest) throws Exception {
        String token = seatReservationRequest.getToken();
        UUID uuid = seatReservationRequest.getUuid();
        long concertScheduleId = seatReservationRequest.getConcertScheduleId();
        long number = seatReservationRequest.getNumber();

        seatFacadeService.createSeatReservation(token, uuid, concertScheduleId, number);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
