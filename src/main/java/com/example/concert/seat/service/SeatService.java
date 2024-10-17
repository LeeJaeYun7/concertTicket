package com.example.concert.seat.service;

import com.example.concert.seat.domain.Seat;
import com.example.concert.seat.domain.SeatStatus;
import com.example.concert.seat.repository.SeatRepository;
import com.example.concert.utils.TimeProvider;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SeatService {

    private final TimeProvider timeProvider;
    private final SeatRepository seatRepository;

    public SeatService(TimeProvider timeProvider, SeatRepository seatRepository){
        this.timeProvider = timeProvider;
        this.seatRepository = seatRepository;
    }

    public List<Seat> getAllAvailableSeats(long concertScheduleId){
        LocalDateTime now = timeProvider.now();
        LocalDateTime threshold = now.minusMinutes(5);

        return seatRepository.findAllAvailableSeatsByConcertScheduleIdAndStatus(concertScheduleId, SeatStatus.AVAILABLE, threshold);
    }

    public void changeUpdatedAt(long concertScheduleId, long number) throws Exception {
        Seat seat = getSeatByConcertScheduleIdAndNumberWithLock(concertScheduleId, number);
        LocalDateTime now = timeProvider.now();
        seat.changeUpdatedAt(now);
    }
    public void updateSeatStatus(long concertScheduleId, long number, SeatStatus status) throws Exception {
        Seat seat = getSeatByConcertScheduleIdAndNumberWithLock(concertScheduleId, number);
        seat.updateStatus(status);
    }

    public Seat getSeatByConcertScheduleIdAndNumber(long concertScheduleId, long number) throws Exception {
        return seatRepository.findByConcertScheduleIdAndNumber(concertScheduleId, number)
                             .orElseThrow(Exception::new);
    }

    public Seat getSeatByConcertScheduleIdAndNumberWithLock(long concertScheduleId, long number) throws Exception {
        return seatRepository.findByConcertScheduleIdAndNumberWithLock(concertScheduleId, number)
                             .orElseThrow(Exception::new);
    }
}
