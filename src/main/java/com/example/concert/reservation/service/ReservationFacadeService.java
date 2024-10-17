package com.example.concert.reservation.service;

import com.example.concert.concert.domain.Concert;
import com.example.concert.concert.service.ConcertService;
import com.example.concert.concertschedule.domain.ConcertSchedule;
import com.example.concert.concertschedule.service.ConcertScheduleService;
import com.example.concert.member.domain.Member;
import com.example.concert.member.service.MemberService;
import com.example.concert.payment.service.PaymentService;
import com.example.concert.reservation.dto.ReservationResponse;
import com.example.concert.seat.domain.Seat;
import com.example.concert.seat.domain.SeatStatus;
import com.example.concert.seat.service.SeatService;
import com.example.concert.utils.TokenValidator;
import com.example.concert.waitingQueue.service.WaitingQueueService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ReservationFacadeService {

    private final TokenValidator tokenValidator;
    private final MemberService memberService;
    private final ReservationService reservationService;
    private final SeatService seatService;

    private final ConcertService concertService;
    private final ConcertScheduleService concertScheduleService;
    private final PaymentService paymentService;

    private final WaitingQueueService waitingQueueService;

    public ReservationFacadeService(TokenValidator tokenValidator, MemberService memberService, ReservationService reservationService, SeatService seatService, ConcertService concertService, ConcertScheduleService concertScheduleService, PaymentService paymentService, WaitingQueueService waitingQueueService){
        this.tokenValidator = tokenValidator;
        this.memberService = memberService;
        this.reservationService = reservationService;
        this.seatService = seatService;
        this.concertService = concertService;
        this.concertScheduleService = concertScheduleService;
        this.paymentService = paymentService;
        this.waitingQueueService = waitingQueueService;
    }

    @Transactional
    public ReservationResponse createReservation(String token, UUID uuid, long concertScheduleId, long seatNumber) throws Exception {
        validateToken(token);
        validateSeatReservation(concertScheduleId, seatNumber);
        checkBalanceOverPrice(uuid, concertScheduleId);

        ConcertSchedule concertSchedule = getConcertSchedule(concertScheduleId);
        Seat seat = seatService.getSeatByConcertScheduleIdAndNumberWithLock(concertScheduleId, seatNumber);
        long price = getConcertSchedule(concertScheduleId).getPrice();

        reservationService.createReservation(concertSchedule, uuid, seat, price);
        paymentService.createPayment(concertSchedule, uuid, price);
        memberService.decreaseBalance(uuid, price);

        updateStatus(token, concertScheduleId, seatNumber);

        String name = getMember(uuid).getName();
        String concertName = getConcert(concertScheduleId).getName();
        LocalDateTime dateTime = getConcertSchedule(concertScheduleId).getDateTime();

        return ReservationResponse.of(name, concertName, dateTime, price);
    }

    private void updateStatus(String token, long concertScheduleId, long seatNumber) throws Exception {
        seatService.updateSeatStatus(concertScheduleId, seatNumber, SeatStatus.RESERVED);
        waitingQueueService.updateWaitingQueueStatus(token);
    }

    private void validateToken(String token) throws Exception {
        boolean isValid = tokenValidator.validateToken(token);

        if(!isValid){
            throw new Exception();
        }
    }

    private void validateSeatReservation(long concertScheduleId, long seatNumber) throws Exception {
         Seat seat = seatService.getSeatByConcertScheduleIdAndNumber(concertScheduleId, seatNumber);

         if(isFiveMinutesPassed(seat.getUpdatedAt())){
             throw new Exception();
         }
    }

    private void checkBalanceOverPrice(UUID uuid, long concertScheduleId) throws Exception {
        long balance = getMember(uuid).getBalance();
        long price = getConcertSchedule(concertScheduleId).getPrice();

        if(balance - price < 0){
            throw new Exception();
        }
    }
    private Member getMember(UUID uuid) throws Exception {
        return memberService.getMemberByUuid(uuid);
    }

    private Concert getConcert(long concertScheduleId) throws Exception {
        ConcertSchedule concertSchedule = getConcertSchedule(concertScheduleId);
        return concertService.getConcertById(concertSchedule.getConcert().getId());
    }

    private ConcertSchedule getConcertSchedule(long concertScheduleId) throws Exception {
        return concertScheduleService.getConcertScheduleById(concertScheduleId);
    }

    private boolean isFiveMinutesPassed(LocalDateTime updatedAt) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(updatedAt, now);
        return duration.toMinutes() >= 5;
    }
}
