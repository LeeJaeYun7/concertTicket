package concert.domain.order.txservices;

import com.fasterxml.jackson.core.JsonProcessingException;
import concert.domain.concert.entities.ConcertEntity;
import concert.domain.concert.entities.ConcertScheduleEntity;
import concert.domain.concert.entities.enums.ConcertScheduleSeatStatus;
import concert.domain.concert.services.ConcertScheduleService;
import concert.domain.concert.services.ConcertService;
import concert.domain.member.entities.MemberEntity;
import concert.domain.member.services.MemberService;
import concert.domain.order.command.PaymentConfirmedCommand;
import concert.domain.order.entities.OrderEntity;
import concert.domain.order.entities.dao.OrderEntityDAO;
import concert.domain.order.entities.enums.OrderStatus;
import concert.domain.order.services.ReservationService;
import concert.domain.order.vo.OrderVO;
import concert.domain.concert.services.ConcertScheduleSeatService;
import concert.domain.shared.utils.DomainJsonConverter;
import concert.domain.waitingqueue.entities.dao.ActiveQueueDAO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderTxService {

  private final MemberService memberService;
  private final ConcertService concertService;
  private final ConcertScheduleSeatService concertScheduleSeatService;
  private final ConcertScheduleService concertScheduleService;
  private final ReservationService reservationService;
  private final OrderEntityDAO orderEntityDAO;
  private final ActiveQueueDAO activeQueueDAO;

  @Transactional
  public void createOrder(long concertId, long concertScheduleId, String uuid, List<Long> concertScheduleSeatIds, long totalPrice) {

    OrderEntity order = OrderEntity.of(concertId, concertScheduleId, uuid, OrderStatus.ACTIVE, totalPrice);
    OrderEntity savedOrder = orderEntityDAO.save(order);

    for(long concertScheduleSeatId: concertScheduleSeatIds){
        reservationService.createReservation(savedOrder.getId(), concertId, concertScheduleSeatId);
    }
  }

  @Transactional
  public OrderVO handlePaymentConfirmed(PaymentConfirmedCommand command) {

    long concertScheduleId = command.getConcertScheduleId();
    String uuid = command.getUuid();
    List<Long> concertScheduleSeatIds = command.getConcertScheduleSeatIds();
    long totalPrice = command.getTotalPrice();

    ConcertScheduleEntity concertSchedule = getConcertSchedule(concertScheduleId);
    long concertId = concertSchedule.getConcertId();

    for(long concertScheduleSeatId: concertScheduleSeatIds){
        updateConcertScheduleSeatStatus(concertScheduleSeatId);
    }

    createOrder(concertId, concertScheduleId, uuid, concertScheduleSeatIds, totalPrice);

    String name = getMember(uuid).getName();
    String concertName = getConcert(concertScheduleId).getName();
    LocalDateTime dateTime = getConcertSchedule(concertScheduleId).getDateTime();

    activeQueueDAO.deleteActiveQueueToken(uuid);

    return OrderVO.of(name, concertName, dateTime, totalPrice);
  }

  private ConcertEntity getConcert(long concertScheduleId) {
    ConcertScheduleEntity concertSchedule = getConcertSchedule(concertScheduleId);
    return concertService.getConcertById(concertSchedule.getConcertId());
  }

  private MemberEntity getMember(String uuid) {
    return memberService.getMemberByUuid(uuid);
  }


  private ConcertScheduleEntity getConcertSchedule(long concertScheduleId) {
    return concertScheduleService.getConcertScheduleById(concertScheduleId);
  }

  private void updateConcertScheduleSeatStatus(long concertScheduleSeatId) {
    concertScheduleSeatService.updateConcertScheduleSeatStatus(concertScheduleSeatId, ConcertScheduleSeatStatus.RESERVED);
  }
}
