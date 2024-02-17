package com.kafka.example.dispatch.handler;

import com.kafka.example.dispatch.dto.in.OrderCreatedDTO;
import com.kafka.example.dispatch.dto.in.OrderUpdatedDTO;
import com.kafka.example.dispatch.exceptions.NotRetryableException;
import com.kafka.example.dispatch.exceptions.RetryableException;
import com.kafka.example.dispatch.service.DispatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

@Slf4j
@RequiredArgsConstructor
@Component
@KafkaListener(
        topics = "order.created"
)
public class OrderHandler {

    private final DispatchService dispatchService;

    @KafkaHandler
    public void listenOrderCreated(@Header(KafkaHeaders.RECEIVED_PARTITION) Integer partition, @Header(KafkaHeaders.RECEIVED_KEY) String key, @Payload OrderCreatedDTO payload) {
        log.info("Order created -> Received message: partition: "+partition+" - key: " +key+ " - orderId: " + payload.getOrderId() + " - item: " + payload.getItem());
        try {
            dispatchService.process(key,payload);
        } catch (Exception e) {
            log.error("Processing failure", e);
        }
    }
    @KafkaHandler
    public void listenOrderUpdated(@Header(KafkaHeaders.RECEIVED_PARTITION) Integer partition, @Header(KafkaHeaders.RECEIVED_KEY) String key, @Payload(expression = "") OrderUpdatedDTO payload) {
        log.info("Order updated -> Received message: partition: "+partition+" - key: " +key  + "updated by" + payload.getUpdatedBy());
        throw new RetryableException();
    }
}
