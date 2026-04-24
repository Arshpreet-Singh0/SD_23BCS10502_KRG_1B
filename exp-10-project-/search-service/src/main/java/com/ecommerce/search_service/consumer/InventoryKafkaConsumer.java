package com.ecommerce.search_service.consumer;

import com.ecommerce.inventory_service.event.InventoryUpdatedEvent;
import com.ecommerce.search_service.services.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class InventoryKafkaConsumer {
    private final SearchService searchService;
    @KafkaListener(topics = "inventory-updated-topic")
    public void listen(InventoryUpdatedEvent event) {
        log.info("Received event: {}", event);
        searchService.updateInventory(event);
    }
}
