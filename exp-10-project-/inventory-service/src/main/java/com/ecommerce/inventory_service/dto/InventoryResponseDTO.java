package com.ecommerce.inventory_service.dto;

import lombok.Data;

@Data
public class InventoryResponseDTO {
    private Long id;
    private String sku;
    private Integer totalQuantity;
    private Integer availableQuantity;
    private Integer reservedQuantity;
}