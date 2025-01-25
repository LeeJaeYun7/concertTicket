package concert.application.reservation.application.facade;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import concert.application.reservation.application.event.PaymentConfirmedEvent;
import concert.application.reservation.application.event.PaymentRequestEvent;
import concert.application.reservation.application.kafka.KafkaMessageProducer;
import concert.commons.common.CustomException;
import concert.commons.common.ErrorCode;
import concert.commons.common.Loggable;
import concert.commons.utils.TimeProvider;
import concert.domain.concert.application.ConcertService;
import concert.domain.concert.domain.Concert;
import concert.domain.concertschedule.application.ConcertScheduleService;
import concert.domain.concertschedule.domain.ConcertSchedule;
import concert.domain.concertscheduleseat.domain.ConcertScheduleSeat;
import concert.domain.member.service.MemberService;
import concert.domain.member.entity.Member;
import concert.domain.reservation.application.ReservationService;
import concert.domain.reservation.domain.Outbox;
import concert.domain.reservation.domain.OutboxRepository;
import concert.domain.reservation.domain.vo.PaymentConfirmedVO;
import concert.domain.reservation.domain.vo.ReservationVO;
import concert.domain.concertscheduleseat.application.ConcertScheduleSeatService;
import concert.domain.seatgrade.domain.application.SeatGradeService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationFacade {

  private final TimeProvider timeProvider;
  private final ReservationService reservationService;
  private final MemberService memberService;
  private final ConcertScheduleSeatService concertScheduleSeatService;
  private final SeatGradeService seatGradeService;

  private final ConcertService concertService;
  private final ConcertScheduleService concertScheduleService;
  private final OutboxRepository outboxRepository;
  private final KafkaMessageProducer kafkaMessageProducer;

  @Getter
  private final CompletableFuture<ReservationVO> reservationFuture = new CompletableFuture<>();

  @Transactional
  public CompletableFuture<ReservationVO> createReservation(String uuid, long concertScheduleId, long concertHallSeatId) throws JsonProcessingException {
    ConcertScheduleSeat concertScheduleSeat = concertScheduleSeatService.getConcertScheduleSeatWithDistributedLock(concertScheduleId, concertHallSeatId);

    long seatGradeId = concertScheduleSeat.getSeatGradeId();
    long price = seatGradeService.getSeatGradePrice(seatGradeId);

    validateSeatReservation(concertScheduleId, concertHallSeatId);
    checkBalanceOverPrice(uuid, price);

    ConcertSchedule concertSchedule = getConcertSchedule(concertScheduleId);

    PaymentRequestEvent event = PaymentRequestEvent.builder()
                                                   .concertId(concertSchedule.getConcertId())
                                                   .concertScheduleId(concertSchedule.getId())
                                                   .uuid(uuid)
                                                   .price(price)
                                                   .build();

    ObjectMapper objectMapper = new ObjectMapper();
    String eventJson = objectMapper.writeValueAsString(event);

    Outbox outbox = Outbox.of("reservation", "payment-request-topic", "PaymentRequest", eventJson, false);
    outboxRepository.save(outbox);

    return reservationFuture;
  }

  private void validateSeatReservation(long concertScheduleId, long concertHallSeatId) {
    ConcertScheduleSeat concertScheduleSeat = concertScheduleSeatService.getConcertScheduleSeat(concertScheduleId, concertHallSeatId);

    if (isFiveMinutesPassed(concertScheduleSeat.getUpdatedAt())) {
      throw new CustomException(ErrorCode.SEAT_RESERVATION_EXPIRED, Loggable.ALWAYS);
    }
  }

  private void checkBalanceOverPrice(String uuid, long price) {
    long balance = getMember(uuid).getBalance();

    if (balance - price < 0) {
      throw new CustomException(ErrorCode.INSUFFICIENT_BALANCE, Loggable.NEVER);
    }
  }

  public void handlePaymentConfirmed(PaymentConfirmedEvent event) {

    long concertId = event.getConcertId();
    long concertScheduleId = event.getConcertScheduleId();
    String uuid = event.getUuid();
    long seatNumber = event.getSeatNumber();
    long price = event.getPrice();

    PaymentConfirmedVO vo = PaymentConfirmedVO.of(concertId, concertScheduleId, uuid, seatNumber, price);

    try {
      reservationService.handlePaymentConfirmed(vo);
    } catch (Exception e) {
      kafkaMessageProducer.sendPaymentConfirmedEvent("payment-compensation-topic", event);
      throw new CustomException(ErrorCode.RESERVATION_FAILED, Loggable.ALWAYS);
    }
  }


  private Concert getConcert(long concertScheduleId) {
    ConcertSchedule concertSchedule = getConcertSchedule(concertScheduleId);
    return concertService.getConcertById(concertSchedule.getConcertId());
  }

  private ConcertSchedule getConcertSchedule(long concertScheduleId) {
    return concertScheduleService.getConcertScheduleById(concertScheduleId);
  }

  private Member getMember(String uuid) {
    return memberService.getMemberByUuid(uuid);
  }


  private boolean isFiveMinutesPassed(LocalDateTime updatedAt) {
    LocalDateTime now = timeProvider.now();
    Duration duration = Duration.between(updatedAt, now);
    return duration.toMinutes() >= 5;
  }

}
