package com.kafka.example.dispatch.service;

import com.kafka.example.dispatch.dto.OrderCreatedDTO;
import com.kafka.example.dispatch.dto.OrderDispatchedDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Slf4j
@RequiredArgsConstructor
@Service
public class DispatchService {
    private static final String ORDER_DISPATCHED_TOPIC = "order.dispatched";
    private final KafkaTemplate<String, Object> kafkaProducer;

    public void process(OrderCreatedDTO orderCreated) throws Exception {
        OrderDispatchedDTO orderDispatched = OrderDispatchedDTO.builder()
                .orderId(orderCreated.getOrderId())
                .build();
        kafkaProducer.send(ORDER_DISPATCHED_TOPIC, orderDispatched.getOrderId().toString(), orderDispatched).get();
    }
}
