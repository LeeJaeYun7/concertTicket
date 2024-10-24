package com.example.concert.seat.service;

import com.example.concert.common.CustomException;
import com.example.concert.common.ErrorCode;
import com.example.concert.common.Loggable;
import com.example.concert.concertschedule.service.ConcertScheduleService;
import com.example.concert.member.service.MemberService;
import com.example.concert.seat.domain.Seat;
import com.example.concert.seat.domain.SeatStatus;
import com.example.concert.utils.TimeProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class SeatFacade {

    private final TimeProvider timeProvider;
    private final MemberService memberService;
    private final ConcertScheduleService concertScheduleService;

    private final SeatService seatService;

    public SeatFacade(TimeProvider timeProvider, MemberService memberService, ConcertScheduleService concertScheduleService, SeatService seatService){
        this.timeProvider = timeProvider;
        this.memberService = memberService;
        this.concertScheduleService = concertScheduleService;
        this.seatService = seatService;
    }

    @Transactional
    public void createSeatReservation(String uuid, long concertScheduleId, long number) {
        validateMember(uuid);
        validateConcertSchedule(concertScheduleId);

        boolean isReservable = validateSeat(concertScheduleId, number);

        if(!isReservable){
            throw new CustomException(ErrorCode.NOT_VALID_SEAT, Loggable.ALWAYS);
        }

        seatService.changeUpdatedAt(concertScheduleId, number);
    }

    private void validateMember(String uuid) {
        memberService.getMemberByUuid(uuid);
    }

    private void validateConcertSchedule(long concertScheduleId) {
        concertScheduleService.getConcertScheduleById(concertScheduleId);
    }

    private boolean validateSeat(long concertScheduleId, long number) {
        Seat seat = seatService.getSeatByConcertScheduleIdAndNumberWithLock(concertScheduleId, number);
        return seat.getStatus().equals(SeatStatus.AVAILABLE) && isFiveMinutesPassed(seat.getUpdatedAt());
    }

    private boolean isFiveMinutesPassed(LocalDateTime updatedAt) {
        LocalDateTime now = timeProvider.now();
        Duration duration = Duration.between(updatedAt, now);
        return duration.toMinutes() >= 5;
    }
}
