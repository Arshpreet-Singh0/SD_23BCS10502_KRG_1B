package com.ecommerce.inventory_service.dto;

import lombok.Data;

@Data
public class InventoryUpdateRequest {

    private String sku;
    private Integer quantity;
}