package com.kafka.example.dispatch.service;

import com.kafka.example.dispatch.dto.OrderCreatedDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class DispatchService {
    public void process(OrderCreatedDTO payload) {
        log.info("Processing message " + payload);
    }
}
