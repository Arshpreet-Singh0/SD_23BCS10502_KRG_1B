package com.ecommerce.product_service.exceptions;

public class ProductSkuAlreadyExistsException extends RuntimeException {
    public ProductSkuAlreadyExistsException(String message) {
        super(message);
    }
}
