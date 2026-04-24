package com.ecommerce.inventory_service.dto;

import lombok.Data;

import java.util.List;

@Data
public class ReserveInventoryDTO {
    private String orderId;
    private List<Item> items;

    @Data
    public static class Item {
        private String sku;
        private int qty;
    }
}
