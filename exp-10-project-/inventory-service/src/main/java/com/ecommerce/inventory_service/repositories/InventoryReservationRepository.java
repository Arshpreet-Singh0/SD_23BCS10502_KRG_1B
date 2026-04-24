package com.ecommerce.inventory_service.repositories;

import com.ecommerce.inventory_service.entities.InventoryReservation;
import com.ecommerce.inventory_service.entities.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface InventoryReservationRepository extends JpaRepository<InventoryReservation, Long> {
    List<InventoryReservation> findByStatusAndExpiresAtBefore(Status status, LocalDateTime expiresAtBefore);
}