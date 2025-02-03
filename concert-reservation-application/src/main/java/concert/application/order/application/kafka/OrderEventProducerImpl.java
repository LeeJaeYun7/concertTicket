package concert.application.order.application.kafka;

import concert.application.order.OrderConst;
import concert.application.order.event.OrderCompensationEvent;
import concert.application.order.event.OrderRequestEvent;
import concert.application.shared.utils.ApplicationJsonConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventProducerImpl implements OrderEventProducer {

  private final ApplicationJsonConverter applicationJsonConverter;
  private final KafkaTemplate<String, String> kafkaTemplate;

  public void sendOrderRequestEvent(OrderRequestEvent event) {
    String eventJson = applicationJsonConverter.convertToJson(event);
    kafkaTemplate.send(OrderConst.ORDER_REQUEST_TOPIC, eventJson);
  }

  public void sendOrderCompensationEvent(OrderCompensationEvent event) {
    String eventJson = applicationJsonConverter.convertToJson(event);
    kafkaTemplate.send(OrderConst.ORDER_COMPENSATION_TOPIC, eventJson);
  }
}
