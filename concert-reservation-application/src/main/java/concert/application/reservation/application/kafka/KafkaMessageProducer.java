package concert.application.reservation.application.kafka;

import concert.application.reservation.application.event.PaymentConfirmedEvent;
import concert.application.reservation.application.event.PaymentRequestEvent;

public interface KafkaMessageProducer {
    void sendPaymentRequestEvent(String topic, PaymentRequestEvent event);
    void sendPaymentConfirmedEvent(String topic, PaymentConfirmedEvent event);
}
