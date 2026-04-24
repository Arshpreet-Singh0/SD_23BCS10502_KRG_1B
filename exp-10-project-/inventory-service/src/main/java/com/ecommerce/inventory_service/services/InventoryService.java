package com.ecommerce.inventory_service.services;

import com.ecommerce.inventory_service.dto.InventoryResponseDTO;
import com.ecommerce.inventory_service.dto.InventoryUpdateRequest;
import com.ecommerce.inventory_service.entities.Inventory;
import com.ecommerce.inventory_service.event.InventoryUpdatedEvent;
import com.ecommerce.inventory_service.repositories.InventoryRepository;
import com.ecommerce.product_service.event.ProductCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private final ModelMapper modelMapper;
    private final KafkaTemplate<String, InventoryUpdatedEvent> kafkaTemplate;
    @Value("${kafka.topic.inventory-updated-topic}")
    private String KAFKA_INVENTORY_UPDATED_TOPIC;

    public InventoryResponseDTO getInventory(String sku) {
        log.info("Getting inventory for sku {}", sku);
        Inventory inventory = inventoryRepository.findBySku(sku).orElseThrow(()->new NoSuchElementException("sku not found"));

        return modelMapper.map(inventory, InventoryResponseDTO.class);
    }

    public void createInventory(ProductCreatedEvent  productCreatedEvent) {
        boolean isExist = inventoryRepository.findBySku(productCreatedEvent.getSku()).isPresent();
        if (isExist) {
            log.info("Inventory already exists for SKU {}, skipping", productCreatedEvent.getSku());
            return;
        }
        Inventory inventory = modelMapper.map(productCreatedEvent, Inventory.class);
        inventory.setId(null);
        inventory.setReservedQuantity(0);
        Inventory i = inventoryRepository.save(inventory);

        log.info("Created inventory for sku {}", i.getId());
    }

    @Transactional
    public void updateInventory(InventoryUpdateRequest request) {

        Inventory inventory = inventoryRepository.findBySku(request.getSku())
                .orElseThrow(() -> new RuntimeException("SKU not found"));

        inventory.setTotalQuantity(request.getQuantity());

        inventoryRepository.save(inventory);

        InventoryUpdatedEvent event =
                new InventoryUpdatedEvent(inventory.getSku(), inventory.getAvailableQuantity());

        kafkaTemplate.send(KAFKA_INVENTORY_UPDATED_TOPIC, inventory.getSku(), event);
    }

}
