package concert.application.order.application.kafka;

import concert.application.order.event.OrderCompensationEvent;
import concert.application.order.event.OrderRequestEvent;

public interface OrderEventProducer {
    void sendOrderRequestEvent(OrderRequestEvent event);
    void sendOrderCompensationEvent(OrderCompensationEvent event);
}
