package com.kafka.example.dispatch.service;

import com.kafka.example.dispatch.dto.DispatchPreparingDTO;
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
import static org.mockito.Mockito.verifyNoMoreInteractions;

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
        when(kafkaProducerMock.send(anyString(), anyString(), any(DispatchPreparingDTO.class))).thenReturn(sendReturnFuture);
        when(kafkaProducerMock.send(anyString(), anyString(), any(OrderDispatchedDTO.class))).thenReturn(sendReturnFuture);
        UUID id = UUID.randomUUID();
        String key = UUID.randomUUID().toString();

        OrderCreatedDTO testEvent = OrderCreatedDTO.builder()
                .item("Prduct 1")
                .orderId(id).build();
        service.process(key, testEvent);
        verify(kafkaProducerMock, times(1))
                .send(eq("dispatch.tracking"), eq(key), any(DispatchPreparingDTO.class));
        verify(kafkaProducerMock, times(1))
                .send(eq("order.dispatched"), eq(key), any(OrderDispatchedDTO.class));
    }

    @Test
    public void testProcess_DispatchTrackingProducerThrowsException() {
        UUID id = UUID.randomUUID();
        String key = UUID.randomUUID().toString();
        OrderCreatedDTO testEvent = OrderCreatedDTO.builder()
                .item("Prduct 1")
                .orderId(id).build();
        doThrow(new RuntimeException("dispatch tracking producer failure"))
                .when(kafkaProducerMock).send(eq("dispatch.tracking"),
                        anyString(), any(DispatchPreparingDTO.class));

        Exception exception = assertThrows(RuntimeException.class, () -> service.process(key, testEvent));

        verify(kafkaProducerMock, times(1))
                .send(eq("dispatch.tracking"), eq(key), any(DispatchPreparingDTO.class));
        verifyNoMoreInteractions(kafkaProducerMock);
        assertThat(exception.getMessage(), equalTo("dispatch tracking producer failure"));
    }

    @Test
    public void testProcess_OrderDispatchedProducerThrowsException() {
        UUID id = UUID.randomUUID();
        String key = UUID.randomUUID().toString();

        OrderCreatedDTO testEvent = OrderCreatedDTO.builder()
                .item("Prduct 1")
                .orderId(id).build();
        when(kafkaProducerMock.send(anyString(), anyString(), any(DispatchPreparingDTO.class))).thenReturn(sendReturnFuture);

        doThrow(new RuntimeException("order dispatched producer failure"))
                .when(kafkaProducerMock).send(eq("order.dispatched"), eq(key), any(OrderDispatchedDTO.class));

        Exception exception = assertThrows(RuntimeException.class, () -> service.process(key, testEvent));

        verify(kafkaProducerMock, times(1))
                .send(eq("dispatch.tracking"), eq(key), any(DispatchPreparingDTO.class));
        verify(kafkaProducerMock, times(1))
                .send(eq("order.dispatched"), eq(key), any(OrderDispatchedDTO.class));
        assertThat(exception.getMessage(), equalTo("order dispatched producer failure"));
    }
}