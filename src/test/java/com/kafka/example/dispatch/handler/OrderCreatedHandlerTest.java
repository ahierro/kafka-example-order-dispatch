package com.kafka.example.dispatch.handler;

import com.kafka.example.dispatch.dto.in.OrderCreatedDTO;
import com.kafka.example.dispatch.service.DispatchService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderCreatedHandlerTest {

    @InjectMocks
    private OrderHandler handler;
    @Mock
    private DispatchService dispatchServiceMock;

    @Test
    void listen_Success() throws Exception {
        String key = UUID.randomUUID().toString();
        OrderCreatedDTO orderCreatedDTO = OrderCreatedDTO.builder()
                .item("Prduct 1")
                .orderId(UUID.randomUUID()).build();
        handler.listenOrderCreated(0, key, orderCreatedDTO);
        verify(dispatchServiceMock, times(1)).process(key, orderCreatedDTO);
    }

    @Test
    public void listen_ServiceThrowsException() throws Exception {
        String key = UUID.randomUUID().toString();

        OrderCreatedDTO orderCreatedDTO = OrderCreatedDTO.builder()
                .item("Prduct 1")
                .orderId(UUID.randomUUID()).build();
        doThrow(new RuntimeException("Service failure")).when(dispatchServiceMock).process(key, orderCreatedDTO);

        handler.listenOrderCreated(0, key, orderCreatedDTO);

        verify(dispatchServiceMock, times(1)).process(key, orderCreatedDTO);
    }
}