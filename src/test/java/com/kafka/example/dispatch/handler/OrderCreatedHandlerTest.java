package com.kafka.example.dispatch.handler;

import com.kafka.example.dispatch.dto.OrderCreatedDTO;
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
    private OrderCreatedHandler handler;
    @Mock
    private DispatchService dispatchServiceMock;

    @Test
    void listen_Success() throws Exception {
        OrderCreatedDTO orderCreatedDTO = OrderCreatedDTO.builder()
                .item("Prduct 1")
                .orderId(UUID.randomUUID()).build();
        handler.listen(orderCreatedDTO);
        verify(dispatchServiceMock, times(1)).process(orderCreatedDTO);
    }

    @Test
    public void listen_ServiceThrowsException() throws Exception {
        OrderCreatedDTO orderCreatedDTO = OrderCreatedDTO.builder()
                .item("Prduct 1")
                .orderId(UUID.randomUUID()).build();
        doThrow(new RuntimeException("Service failure")).when(dispatchServiceMock).process(orderCreatedDTO);

        handler.listen(orderCreatedDTO);

        verify(dispatchServiceMock, times(1)).process(orderCreatedDTO);
    }
}