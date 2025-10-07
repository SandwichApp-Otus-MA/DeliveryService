package com.sandwich.app.kafka;

import com.sandwich.app.models.model.enums.OrderStatus;
import com.sandwich.app.models.model.event.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventProducer {

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    @Value("${sandwich.kafka.producer.topic.order}")
    private String orderTopic;

    public void send(UUID orderId, OrderStatus orderStatus) {
        try {
            kafkaTemplate.send(orderTopic, new OrderEvent()
                .setId(orderId)
                .setStatus(orderStatus));
        } catch (Exception ex) {
            log.error("Не удалось отправить событие со статусом заказа: orderId: {}, orderStatus: {}", orderId, orderStatus);
        }
    }
}
