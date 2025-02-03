package concert.application.order.consumer;

import concert.application.order.OrderConst;
import concert.application.order.business.OrderApplicationService;
import concert.application.order.event.PaymentConfirmedEvent;
import concert.application.shared.utils.ApplicationJsonConverter;
import concert.domain.order.entities.OutboxEntity;
import concert.domain.order.exceptions.OrderException;
import concert.domain.order.exceptions.OrderExceptionType;
import concert.domain.order.entities.dao.OutboxEntityDAO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventConsumer {

  private final ApplicationJsonConverter applicationJsonConverter;
  private final OrderApplicationService orderApplicationService;
  private final OutboxEntityDAO outboxEntityDAO;

  @KafkaListener(topics = OrderConst.PAYMENT_CONFIRMED_TOPIC)
  public void receivePaymentConfirmedEvent(String message){

    PaymentConfirmedEvent event = applicationJsonConverter.convertFromJson(message, PaymentConfirmedEvent.class);

    try {
      orderApplicationService.handlePaymentConfirmed(event);
    }catch(OrderException e){
      log.error("handlePaymentConfirmed has an exception", e);
    }

    Optional<OutboxEntity> outboxEvent = outboxEntityDAO.findByMessage(message);

    if (outboxEvent.isPresent()) {
      OutboxEntity outbox = outboxEvent.get();
      outbox.updateSent(true);
      outboxEntityDAO.save(outbox);
    }
  }

  @KafkaListener(topics = OrderConst.PAYMENT_FAILED_TOPIC)
  public void receivePaymentFailedEvent(String message) throws OrderException {
    throw new OrderException(OrderExceptionType.PAYMENT_FAILED);
  }
}
