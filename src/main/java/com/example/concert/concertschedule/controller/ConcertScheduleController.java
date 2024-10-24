package com.example.concert.concertschedule.controller;

import com.example.concert.concertschedule.dto.response.ConcertScheduleResponse;
import com.example.concert.concertschedule.service.ConcertScheduleFacade;
import com.example.concert.seat.dto.response.SeatNumbersResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
public class ConcertScheduleController {

    private final ConcertScheduleFacade concertScheduleFacade;

    public ConcertScheduleController(ConcertScheduleFacade concertScheduleFacade){
        this.concertScheduleFacade = concertScheduleFacade;
    }

    @GetMapping("/concertSchedule")
    public ResponseEntity<ConcertScheduleResponse> retrieveAvailableDateTimes(@RequestParam long concertId) {
        List<LocalDateTime> availableDateTimes = concertScheduleFacade.getAvailableDateTimes(concertId);
        ConcertScheduleResponse concertScheduleResponse = ConcertScheduleResponse.of(availableDateTimes);

        return ResponseEntity.status(HttpStatus.OK).body(concertScheduleResponse);
    }

    @GetMapping("/concertSchedule/seats")
    public ResponseEntity<SeatNumbersResponse> retrieveAvailableSeats(@RequestParam long concertScheduleId) {
        List<Long> availableSeatNumbers = concertScheduleFacade.getAvailableSeatNumbers(concertScheduleId);
        SeatNumbersResponse seatsResponse = SeatNumbersResponse.of(availableSeatNumbers);

        return ResponseEntity.status(HttpStatus.OK).body(seatsResponse);
    }
}
