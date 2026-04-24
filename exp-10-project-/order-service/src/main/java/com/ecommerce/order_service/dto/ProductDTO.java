package com.ecommerce.order_service.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProductDTO {
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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
