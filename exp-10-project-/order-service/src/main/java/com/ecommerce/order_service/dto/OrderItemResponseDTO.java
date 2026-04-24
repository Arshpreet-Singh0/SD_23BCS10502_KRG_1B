package com.ecommerce.order_service.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemResponseDTO {

    private Long productId;
    private String sku;
    private Integer quantity;
    private BigDecimal price;
}