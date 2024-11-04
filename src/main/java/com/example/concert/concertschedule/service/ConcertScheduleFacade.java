package com.example.concert.concertschedule.service;

import com.example.concert.concertschedule.domain.ConcertSchedule;
import com.example.concert.concertschedule.dto.response.AvailableDateTimesResponse;
import com.example.concert.concertschedule.dto.response.ConcertScheduleResponse;
import com.example.concert.seat.domain.Seat;
import com.example.concert.seat.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConcertScheduleFacade {
    private final ConcertScheduleService concertScheduleService;
    private final SeatService seatService;

    public List<ConcertScheduleResponse> getAllAvailableConcertSchedules(){
        return concertScheduleService.getAllAvailableConcertSchedules();
    }


    public List<LocalDateTime> getAvailableDateTimes(long concertId) {

        List<ConcertSchedule> allConcertSchedules = concertScheduleService.getAllConcertSchedulesAfterNowByConcertId(concertId);
        List<LocalDateTime> availableDateTimes = new ArrayList<>();

        for(ConcertSchedule concertSchedule: allConcertSchedules){
            long concertScheduleId = concertSchedule.getId();

            List<Seat> availableSeats = seatService.getAllAvailableSeats(concertScheduleId);

            if(!availableSeats.isEmpty()){
                availableDateTimes.add(concertSchedule.getDateTime());
            }
        }
        return availableDateTimes;
    }

    public List<Long> getAvailableSeatNumbers(long concertScheduleId) {

        List<Seat> availableSeats = seatService.getAllAvailableSeats(concertScheduleId);
        List<Long> availableSeatNumbers = new ArrayList<>();

        for(Seat seat: availableSeats){
            availableSeatNumbers.add(seat.getNumber());
        }

        return availableSeatNumbers;
    }
}
