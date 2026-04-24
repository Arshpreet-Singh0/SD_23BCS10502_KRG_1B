package com.ecommerce.inventory_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Value("${kafka.topic.inventory-updated-topic}")
    private String KAFKA_INVENTORY_UPDATED_TOPIC;

    @Bean
    public NewTopic productCreatedTopic() {
        return new NewTopic(KAFKA_INVENTORY_UPDATED_TOPIC, 3, (short) 1);
    }
}
