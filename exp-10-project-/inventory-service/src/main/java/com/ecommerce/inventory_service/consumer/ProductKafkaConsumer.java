package com.ecommerce.inventory_service.consumer;

import com.ecommerce.inventory_service.services.InventoryService;
import com.ecommerce.product_service.event.ProductCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductKafkaConsumer {

    private final InventoryService inventoryService;


    @KafkaListener(topics = "product-created-topic")
    public void listen(ProductCreatedEvent productCreatedEvent) {
        log.info("Received event: {}", productCreatedEvent);
        inventoryService.createInventory(productCreatedEvent);
    }
}
