package com.kafka.example.dispatch.integration;

import com.kafka.example.dispatch.DispatchConfiguration;
import com.kafka.example.dispatch.dto.OrderCreatedDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.kafka.example.dispatch.integration.TestConstants.ORDER_CREATED_TOPIC;
import static java.util.UUID.randomUUID;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;

@Slf4j
@SpringBootTest(classes = {DispatchConfiguration.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)//context reset after the current test class
@ActiveProfiles("test")//Pulls configuration from application-test.properties
@EmbeddedKafka(controlledShutdown = true, partitions = 1)
public class OrderDispatchIntegrationTest {

    @Autowired
    private KafkaTemplate<String,Object> kafkaTemplate;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private KafkaListenerEndpointRegistry registry;

    @Autowired
    private KafkaTestListener testListener;

    @BeforeEach
    public void setUp() {

        testListener.resetCounters();

        // Wait until the partitions are assigned.
        registry.getListenerContainers().forEach(container ->
                ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic()));
    }

    /**
     * Send in an order.created event and ensure the expected outbound events are emitted.
     */
    @Test
    public void testOrderDispatchFlow() throws Exception {
        UUID id = randomUUID();
        OrderCreatedDTO orderCreated = OrderCreatedDTO.builder()
                .item("my-item")
                .orderId(id).build();
        sendMessage(ORDER_CREATED_TOPIC, randomUUID().toString(), orderCreated);

        await().atMost(3, TimeUnit.SECONDS).pollDelay(100, TimeUnit.MILLISECONDS)
                .until(testListener.dispatchPreparingCounter()::get, equalTo(1));
        await().atMost(1, TimeUnit.SECONDS).pollDelay(100, TimeUnit.MILLISECONDS)
                .until(testListener.orderDispatchedCounter()::get, equalTo(1));
    }

    private void sendMessage(String topic, String key, Object data) throws Exception {
        kafkaTemplate.send(MessageBuilder
                .withPayload(data)
                .setHeader(KafkaHeaders.KEY, key)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .build()).get();
    }
}
