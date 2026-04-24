package com.ecommerce.inventory_service.services;

import com.ecommerce.inventory_service.dto.ReserveInventoryDTO;
import com.ecommerce.inventory_service.entities.Inventory;
import com.ecommerce.inventory_service.entities.InventoryReservation;
import com.ecommerce.inventory_service.entities.enums.Status;
import com.ecommerce.inventory_service.repositories.InventoryRepository;
import com.ecommerce.inventory_service.repositories.InventoryReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryReservationService {

    private final InventoryReservationRepository inventoryReservationRepository;
    private final InventoryRepository inventoryRepository;

    @Transactional
    public void reserveInventory(ReserveInventoryDTO dto) {

        // 1. Extract SKUs
        List<String> skus = dto.getItems()
                .stream()
                .map(ReserveInventoryDTO.Item::getSku)
                .toList();

        // 2. Fetch all inventories in one query
        List<Inventory> inventories = inventoryRepository.findBySkuIn(skus);

        Map<String, Inventory> inventoryMap = inventories.stream()
                .collect(Collectors.toMap(Inventory::getSku, i -> i));

        // 3. VALIDATION (very important)
        for (ReserveInventoryDTO.Item item : dto.getItems()) {

            Inventory inventory = inventoryMap.get(item.getSku());

            if (inventory == null) {
                throw new RuntimeException("SKU not found: " + item.getSku());
            }

            if (inventory.getAvailableQuantity() < item.getQty()) {
                throw new RuntimeException("Insufficient inventory for SKU: " + item.getSku());
            }
        }

        // 4. UPDATE (only after all validations pass)
        List<InventoryReservation> reservations = new ArrayList<>();

        for (ReserveInventoryDTO.Item item : dto.getItems()) {

            Inventory inventory = inventoryMap.get(item.getSku());

            inventory.setReservedQuantity(
                    inventory.getReservedQuantity() + item.getQty()
            );

            InventoryReservation reservation = InventoryReservation.builder()
                    .sku(item.getSku())
                    .quantity(item.getQty())
                    .orderId(dto.getOrderId())
                    .status(Status.RESERVED)
                    .expiresAt(LocalDateTime.now().plusMinutes(5))
                    .build();

            reservations.add(reservation);
        }

        // 5. SAVE (batch)
        inventoryRepository.saveAll(inventories);
        inventoryReservationRepository.saveAll(reservations);
    }
}