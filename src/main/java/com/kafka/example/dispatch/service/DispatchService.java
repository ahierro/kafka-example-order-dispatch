package com.kafka.example.dispatch.service;

import com.kafka.example.dispatch.dto.DispatchPreparingDTO;
import com.kafka.example.dispatch.dto.OrderCreatedDTO;
import com.kafka.example.dispatch.dto.OrderDispatchedDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static java.util.UUID.randomUUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class DispatchService {
    private static final String ORDER_DISPATCHED_TOPIC = "order.dispatched";
    private static final String DISPATCH_TRACKING_TOPIC = "dispatch.tracking";
    private static final UUID APPLICATION_ID = randomUUID();

    private final KafkaTemplate<String, Object> kafkaProducer;

    public void process(String key, OrderCreatedDTO orderCreated) throws Exception {

        DispatchPreparingDTO dispatchPreparingDTO = DispatchPreparingDTO.builder()
                .orderId(orderCreated.getOrderId())
                .build();
        kafkaProducer.send(DISPATCH_TRACKING_TOPIC, key,dispatchPreparingDTO).get();

        OrderDispatchedDTO orderDispatched = OrderDispatchedDTO.builder()
                .orderId(orderCreated.getOrderId())
                .processedById(APPLICATION_ID)
                .notes("Dispatched: " + orderCreated.getItem())
                .build();
        kafkaProducer.send(ORDER_DISPATCHED_TOPIC, key, orderDispatched).get();
        log.info("Sent messages: key: " + key + " - orderId: " + orderCreated.getOrderId() + " - processedById: " + APPLICATION_ID);
    }
}
