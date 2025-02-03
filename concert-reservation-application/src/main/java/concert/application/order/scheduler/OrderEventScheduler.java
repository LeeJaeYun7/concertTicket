package concert.application.order.scheduler;

import concert.application.order.application.kafka.OrderEventProducer;
import concert.application.order.event.OrderRequestEvent;
import concert.application.shared.utils.ApplicationJsonConverter;
import concert.domain.order.entities.OutboxEntity;
import concert.domain.order.entities.dao.OutboxEntityDAO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventScheduler {

  private final ApplicationJsonConverter applicationJsonConverter;
  private final OutboxEntityDAO outboxEntityDAO;
  private final OrderEventProducer orderEventProducer;


  @Scheduled(fixedRate = 10000)
  public void publishOrderRequestEvents() {
    log.info("publishOrderRequestEvent 실행");

    List<OutboxEntity> events = outboxEntityDAO.findTop10UnsentEvents();

    if (events.isEmpty()) {
      return;
    }

    for (OutboxEntity event : events) {
      try {
        String eventJson = event.getMessage();
        OrderRequestEvent orderRequestEvent = applicationJsonConverter.convertFromJson(eventJson, OrderRequestEvent.class);
        orderEventProducer.sendOrderRequestEvent(orderRequestEvent);

        event.updateSent(true);
        outboxEntityDAO.save(event);

        log.info("OrderPaymentEvent Sent: eventId={}", event.getId());
      } catch (Exception e) {
        log.error("Failed to send OrderPaymentEvent: eventId={}, error={}", event.getId(), e.getMessage(), e);
      }
    }
  }
}
