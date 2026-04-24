package com.ecommerce.inventory_service.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryUpdatedEvent {

    private String sku;
    private Integer quantity;
}