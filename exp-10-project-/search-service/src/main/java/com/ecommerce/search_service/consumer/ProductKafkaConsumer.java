package com.ecommerce.search_service.consumer;

import com.ecommerce.product_service.event.ProductCreatedEvent;
import com.ecommerce.search_service.services.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductKafkaConsumer {

    private final SearchService searchService;
    @KafkaListener(topics = "product-created-topic")
    public void listen(ProductCreatedEvent productCreatedEvent) {
        log.info("Received event: {}", productCreatedEvent);
        searchService.indexProduct(productCreatedEvent);
    }
}
