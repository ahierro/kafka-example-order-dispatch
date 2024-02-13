package com.kafka.example.dispatch.integration;

import com.kafka.example.dispatch.dto.DispatchPreparingDTO;
import com.kafka.example.dispatch.dto.OrderDispatchedDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;

import java.util.concurrent.atomic.AtomicInteger;

import static com.kafka.example.dispatch.integration.TestConstants.*;

@Slf4j
public record KafkaTestListener(AtomicInteger dispatchPreparingCounter, AtomicInteger orderDispatchedCounter) {
    public KafkaTestListener() {
        this(new AtomicInteger(0),new AtomicInteger(0));
    }

    public void resetCounters(){
        dispatchPreparingCounter.set(0);
        orderDispatchedCounter.set(0);
    }

    @KafkaListener(groupId = "KafkaIntegrationTest", topics = DISPATCH_TRACKING_TOPIC)
    void receiveDispatchPreparing(@Payload DispatchPreparingDTO payload) {
        log.debug("Received DispatchPreparing: " + payload);
        dispatchPreparingCounter.incrementAndGet();
    }

    @KafkaListener(groupId = "KafkaIntegrationTest", topics = ORDER_DISPATCHED_TOPIC)
    void receiveOrderDispatched(@Payload OrderDispatchedDTO payload) {
        log.debug("Received OrderDispatched: " + payload);
        orderDispatchedCounter.incrementAndGet();
    }
}
