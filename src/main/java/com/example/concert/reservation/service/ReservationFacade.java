package com.example.concert.reservation.service;

import com.example.concert.common.CustomException;
import com.example.concert.common.ErrorCode;
import com.example.concert.common.Loggable;
import com.example.concert.concert.domain.Concert;
import com.example.concert.concert.service.ConcertService;
import com.example.concert.concertschedule.domain.ConcertSchedule;
import com.example.concert.concertschedule.service.ConcertScheduleService;
import com.example.concert.member.domain.Member;
import com.example.concert.member.service.MemberService;
import com.example.concert.payment.service.PaymentService;
import com.example.concert.reservation.vo.ReservationVO;
import com.example.concert.seat.domain.Seat;
import com.example.concert.seat.enums.SeatStatus;
import com.example.concert.seat.service.SeatService;
import com.example.concert.utils.TimeProvider;
import com.example.concert.waitingQueue.service.WaitingQueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ReservationFacade {

    private final TimeProvider timeProvider;
    private final MemberService memberService;
    private final ReservationService reservationService;
    private final SeatService seatService;
    private final ConcertService concertService;
    private final ConcertScheduleService concertScheduleService;
    private final PaymentService paymentService;
    private final WaitingQueueService waitingQueueService;

    @Transactional
    public ReservationVO createReservation(String token, String uuid, long concertScheduleId, long seatNumber) {
        validateSeatReservation(concertScheduleId, seatNumber);
        checkBalanceOverPrice(uuid, concertScheduleId);

        ConcertSchedule concertSchedule = getConcertSchedule(concertScheduleId);
        Seat seat = seatService.getSeatByConcertHallIdAndNumberWithPessimisticLock(concertScheduleId, seatNumber);
        long price = getConcertSchedule(concertScheduleId).getPrice();

        reservationService.createReservation(concertSchedule.getConcert(), concertSchedule, uuid, seat, price);
        paymentService.createPayment(concertSchedule.getConcert(), concertSchedule, uuid, price);
        memberService.decreaseBalance(uuid, price);

        updateStatus(token, concertScheduleId, seatNumber);

        String name = getMember(uuid).getName();
        String concertName = getConcert(concertScheduleId).getName();
        LocalDateTime dateTime = getConcertSchedule(concertScheduleId).getDateTime();

        return ReservationVO.of(name, concertName, dateTime, price);
    }

    private void updateStatus(String token, long concertScheduleId, long seatNumber) {
        seatService.updateSeatStatus(concertScheduleId, seatNumber, SeatStatus.RESERVED);
        waitingQueueService.updateWaitingQueueStatus(token);
    }

    private void validateSeatReservation(long concertScheduleId, long seatNumber) {
         Seat seat = seatService.getSeatByConcertHallIdAndNumber(concertScheduleId, seatNumber);

         if(isFiveMinutesPassed(seat.getUpdatedAt())){
             throw new CustomException(ErrorCode.SEAT_RESERVATION_EXPIRED, Loggable.ALWAYS);
         }
    }

    private void checkBalanceOverPrice(String uuid, long concertScheduleId) {
        long balance = getMember(uuid).getBalance();
        long price = getConcertSchedule(concertScheduleId).getPrice();

        if(balance - price < 0){
            throw new CustomException(ErrorCode.INSUFFICIENT_BALANCE, Loggable.NEVER);
        }
    }
    private Member getMember(String uuid) {
        return memberService.getMemberByUuid(uuid);
    }

    private Concert getConcert(long concertScheduleId) {
        ConcertSchedule concertSchedule = getConcertSchedule(concertScheduleId);
        return concertService.getConcertById(concertSchedule.getConcert().getId());
    }

    private ConcertSchedule getConcertSchedule(long concertScheduleId) {
        return concertScheduleService.getConcertScheduleById(concertScheduleId);
    }

    private boolean isFiveMinutesPassed(LocalDateTime updatedAt) {
        LocalDateTime now = timeProvider.now();
        Duration duration = Duration.between(updatedAt, now);
        return duration.toMinutes() >= 5;
    }
}
