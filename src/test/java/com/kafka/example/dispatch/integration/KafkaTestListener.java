package com.kafka.example.dispatch.integration;

import com.kafka.example.dispatch.dto.out.DispatchCompletedDTO;
import com.kafka.example.dispatch.dto.out.DispatchPreparingDTO;
import com.kafka.example.dispatch.dto.out.OrderDispatchedDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

import java.util.concurrent.atomic.AtomicInteger;

import static com.kafka.example.dispatch.integration.TestConstants.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import org.springframework.kafka.annotation.KafkaHandler;

@Slf4j
@KafkaListener(groupId = "KafkaIntegrationTest", topics = { DISPATCH_TRACKING_TOPIC, ORDER_DISPATCHED_TOPIC })
public record KafkaTestListener(AtomicInteger dispatchPreparingCounter, AtomicInteger orderDispatchedCounter, AtomicInteger dispatchCompletedCounter) {
    public KafkaTestListener() {
        this(new AtomicInteger(0), new AtomicInteger(0), new AtomicInteger(0));
    }

    public void resetCounters() {
        dispatchPreparingCounter.set(0);
        orderDispatchedCounter.set(0);
        dispatchCompletedCounter.set(0);
    }

    @KafkaHandler
    void receiveDispatchPreparing(@Header(KafkaHeaders.RECEIVED_KEY) String key, @Payload DispatchPreparingDTO payload) {
        log.info("Received DispatchPreparing key: " + key + " - payload: " + payload);
        assertThat(key, notNullValue());
        assertThat(payload, notNullValue());
        dispatchPreparingCounter.incrementAndGet();
    }

    @KafkaHandler
    void receiveOrderDispatched(@Header(KafkaHeaders.RECEIVED_KEY) String key, @Payload OrderDispatchedDTO payload) {
        log.info("Received OrderDispatched key: " + key + " - payload: " + payload);
        assertThat(key, notNullValue());
        assertThat(payload, notNullValue());
        orderDispatchedCounter.incrementAndGet();
    }

    @KafkaHandler
    void receiveDispatchCompleted(@Header(KafkaHeaders.RECEIVED_KEY) String key, @Payload DispatchCompletedDTO payload) {
        log.info("Received DispatchCompleted key: " + key + " - payload: " + payload);
        assertThat(key, notNullValue());
        assertThat(payload, notNullValue());
        dispatchCompletedCounter.incrementAndGet();
    }
}
