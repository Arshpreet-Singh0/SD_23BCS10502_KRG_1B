package com.ecommerce.inventory_service.repositories;

import com.ecommerce.inventory_service.entities.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findBySku(String sku);

    List<Inventory> findBySkuIn(Collection<String> skus);
}