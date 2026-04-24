package com.ecommerce.product_service.repositories;

import com.ecommerce.product_service.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsBySku(String sku);

    Optional<Product> findBySku(String sku);

}