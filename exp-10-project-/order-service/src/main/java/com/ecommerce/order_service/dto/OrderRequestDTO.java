package com.ecommerce.order_service.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class OrderRequestDTO {
    private BigDecimal totalAmount;
    private String idempotencyKey;
    private List<OrderItemDTO> items;
}
