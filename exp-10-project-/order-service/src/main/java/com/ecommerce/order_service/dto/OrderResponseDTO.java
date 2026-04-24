package com.ecommerce.order_service.dto;

import com.ecommerce.order_service.entities.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderResponseDTO {

    private Long orderId;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private List<OrderItemResponseDTO> items;
}
