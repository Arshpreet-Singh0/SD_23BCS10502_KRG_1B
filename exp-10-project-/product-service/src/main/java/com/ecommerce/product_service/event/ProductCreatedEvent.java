package com.ecommerce.product_service.event;

import lombok.Data;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProductCreatedEvent {
    private Long id;
    private String name;
    private String description;
    private String sku;
    private BigDecimal price;
    private BigDecimal discountPercent;
    private String currency;
    private String category;
    private String brand;
    private List<String> imageUrls;
    private String status;
    private Boolean isVisible;
    private Integer totalQuantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
