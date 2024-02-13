package com.kafka.example.dispatch.handler;

import com.kafka.example.dispatch.dto.OrderCreatedDTO;
import com.kafka.example.dispatch.service.DispatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

@Slf4j
@RequiredArgsConstructor
@Component
public class OrderCreatedHandler {

    private final DispatchService dispatchService;

    @KafkaListener(
            id = "orderConsumerClient",
            topics = "order.created",
            groupId = "dispatch.order.created.consumer",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listen(@Header(KafkaHeaders.RECEIVED_PARTITION) Integer partition, @Header(KafkaHeaders.RECEIVED_KEY) String key, @Payload OrderCreatedDTO payload) {
        log.info("Received message: partition: "+partition+" - key: " +key+ " - orderId: " + payload.getOrderId() + " - item: " + payload.getItem());
        try {
            dispatchService.process(key,payload);
        } catch (Exception e) {
            log.error("Processing failure", e);
        }
    }
}
