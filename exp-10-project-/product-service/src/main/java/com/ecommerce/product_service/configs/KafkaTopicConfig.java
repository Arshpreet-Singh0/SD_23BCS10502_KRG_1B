package com.ecommerce.product_service.configs;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Value("${kafka.topic.product-created-topic}")
    private String KAFKA_PRODUCT_CREATED_TOPIC;

    @Bean
    public NewTopic productCreatedTopic() {
        return new NewTopic(KAFKA_PRODUCT_CREATED_TOPIC, 3, (short) 1);
    }
}
