package com.kafka.example.dispatch.service;

import com.kafka.example.dispatch.dto.OrderCreatedDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DispatchServiceTest {

    @InjectMocks
    DispatchService dispatchService;

    @Test
    void process() {
        OrderCreatedDTO orderCreatedDTO = OrderCreatedDTO.builder()
                .item("Prduct 1")
                .orderId(UUID.randomUUID()).build();
        dispatchService.process(orderCreatedDTO);
    }
}