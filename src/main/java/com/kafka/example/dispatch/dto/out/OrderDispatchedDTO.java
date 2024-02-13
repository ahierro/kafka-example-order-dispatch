package com.kafka.example.dispatch.dto.out;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDispatchedDTO {

    UUID orderId;
    UUID processedById;
    String notes;
}
