package com.ecommerce.search_service.documents;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductSearchDocument {

    private String sku;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal discountPercent;
    private String brand;
    private String category;

    private Boolean inStock;
}