package com.example.concert.seat.service;

import com.example.concert.concertschedule.service.ConcertScheduleService;
import com.example.concert.member.service.MemberService;
import com.example.concert.seat.domain.Seat;
import com.example.concert.seat.domain.SeatStatus;
import com.example.concert.utils.TokenValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class SeatFacadeService {

    private final TokenValidator tokenValidator;
    private final MemberService memberService;
    private final ConcertScheduleService concertScheduleService;

    private final SeatService seatService;

    public SeatFacadeService(TokenValidator tokenValidator, MemberService memberService, ConcertScheduleService concertScheduleService, SeatService seatService){
        this.tokenValidator = tokenValidator;
        this.memberService = memberService;
        this.concertScheduleService = concertScheduleService;
        this.seatService = seatService;
    }

    @Transactional
    public void createSeatReservation(String token, UUID uuid, long concertScheduleId, long number) throws Exception {

        validateMember(uuid);
        validateToken(token);
        validateConcertSchedule(concertScheduleId);

        boolean isReservable = validateSeat(concertScheduleId, number);

        if(isReservable){
            seatService.changeUpdatedAt(concertScheduleId, number);
        }
    }

    private void validateMember(UUID uuid) throws Exception {
        memberService.getMemberByUuid(uuid);
    }

    private void validateToken(String token) throws Exception {
        boolean isValid = tokenValidator.validateToken(token);

        if(!isValid){
            throw new Exception();
        }
    }

    private void validateConcertSchedule(long concertScheduleId) throws Exception {
        concertScheduleService.getConcertScheduleById(concertScheduleId);
    }

    private boolean validateSeat(long concertScheduleId, long number) throws Exception {
        Seat seat = seatService.getSeatByConcertScheduleIdAndNumberWithLock(concertScheduleId, number);
        return seat.getStatus().equals(SeatStatus.AVAILABLE) && isFiveMinutesPassed(seat.getUpdatedAt());
    }

    private boolean isFiveMinutesPassed(LocalDateTime updatedAt) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(updatedAt, now);
        return duration.toMinutes() >= 5;
    }
}
