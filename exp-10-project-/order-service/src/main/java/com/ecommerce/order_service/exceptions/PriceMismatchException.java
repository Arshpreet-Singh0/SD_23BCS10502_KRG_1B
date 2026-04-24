package com.ecommerce.order_service.exceptions;

public class PriceMismatchException extends RuntimeException {
    public PriceMismatchException(String msg) { super(msg); }
}
