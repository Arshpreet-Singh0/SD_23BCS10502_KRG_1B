package com.ecommerce.order_service.entities;

public enum OrderStatus {
    CREATED,
    PAYMENT_PENDING,
    PAID,
    CANCELLED,
    EXPIRED,
    FAILED
}