package com.ecommerce.order_service.dto;

import lombok.Data;

import java.util.List;

@Data
public class ReserveInventoryRequest {
    private String orderId;
    private List<Item> items;

    @Data
    public static class Item {
        private String sku;
        private int qty;
    }
}

