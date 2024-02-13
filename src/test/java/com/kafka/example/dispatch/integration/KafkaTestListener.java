package com.kafka.example.dispatch.integration;

import com.kafka.example.dispatch.dto.DispatchPreparingDTO;
import com.kafka.example.dispatch.dto.OrderDispatchedDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

import java.util.concurrent.atomic.AtomicInteger;

import static com.kafka.example.dispatch.integration.TestConstants.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

@Slf4j
public record KafkaTestListener(AtomicInteger dispatchPreparingCounter, AtomicInteger orderDispatchedCounter) {
    public KafkaTestListener() {
        this(new AtomicInteger(0), new AtomicInteger(0));
    }

    public void resetCounters() {
        dispatchPreparingCounter.set(0);
        orderDispatchedCounter.set(0);
    }

    @KafkaListener(groupId = "KafkaIntegrationTest", topics = DISPATCH_TRACKING_TOPIC)
    void receiveDispatchPreparing(@Header(KafkaHeaders.RECEIVED_KEY) String key, @Payload DispatchPreparingDTO payload) {
        log.debug("Received DispatchPreparing key: " + key + " - payload: " + payload);
        assertThat(key, notNullValue());
        assertThat(payload, notNullValue());
        dispatchPreparingCounter.incrementAndGet();
    }

    @KafkaListener(groupId = "KafkaIntegrationTest", topics = ORDER_DISPATCHED_TOPIC)
    void receiveOrderDispatched(@Header(KafkaHeaders.RECEIVED_KEY) String key, @Payload OrderDispatchedDTO payload) {
        log.debug("Received OrderDispatched key: " + key + " - payload: " + payload);
        assertThat(key, notNullValue());
        assertThat(payload, notNullValue());
        orderDispatchedCounter.incrementAndGet();
    }
}
