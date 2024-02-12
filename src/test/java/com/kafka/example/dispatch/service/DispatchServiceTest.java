package com.kafka.example.dispatch.service;

import com.kafka.example.dispatch.dto.OrderCreatedDTO;
import com.kafka.example.dispatch.dto.OrderDispatchedDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DispatchServiceTest {

    @InjectMocks
    DispatchService service;

    @Mock
    KafkaTemplate<String, Object> kafkaProducerMock;

    @Mock
    private CompletableFuture<SendResult<String, Object>> sendReturnFuture;
    
    @Test
    void process_Success() throws Exception {
        when(kafkaProducerMock.send(anyString(), anyString(), any(OrderDispatchedDTO.class))).thenReturn(sendReturnFuture);
        UUID id = UUID.randomUUID();

        OrderCreatedDTO testEvent = OrderCreatedDTO.builder()
                .item("Prduct 1")
                .orderId(id).build();
        service.process(testEvent);

        verify(kafkaProducerMock, times(1))
                .send(eq("order.dispatched"), eq(id.toString()), any(OrderDispatchedDTO.class));
    }

    @Test
    public void process_ProducerThrowsException() {
        UUID id = UUID.randomUUID();
        OrderCreatedDTO testEvent = OrderCreatedDTO.builder()
                .item("Prduct 1")
                .orderId(id).build();
        doThrow(new RuntimeException("Producer failure")).when(kafkaProducerMock).send(eq("order.dispatched"), anyString(), any(OrderDispatchedDTO.class));

        Exception exception = assertThrows(RuntimeException.class, () -> service.process(testEvent));

        verify(kafkaProducerMock, times(1)).send(eq("order.dispatched"), eq(id.toString()), any(OrderDispatchedDTO.class));
        assertThat(exception.getMessage(), equalTo("Producer failure"));
    }
}