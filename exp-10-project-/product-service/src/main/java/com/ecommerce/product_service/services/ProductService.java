package com.ecommerce.product_service.services;


import com.ecommerce.product_service.dto.CreateProductRequestDTO;
import com.ecommerce.product_service.dto.ProductResponseDTO;
import com.ecommerce.product_service.dto.UpdateProductRequestDTO;
import com.ecommerce.product_service.entities.Product;
import com.ecommerce.product_service.entities.enums.ProductStatus;
import com.ecommerce.product_service.event.ProductCreatedEvent;
import com.ecommerce.product_service.exceptions.ProductSkuAlreadyExistsException;
import com.ecommerce.product_service.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate;

    @Value("${kafka.topic.product-created-topic}")
    private String KAFKA_PRODUCT_CREATED_TOPIC;

    @Transactional
    public ProductResponseDTO create(CreateProductRequestDTO request) {
        boolean isExist = productRepository.existsBySku(request.getSku());

        if (isExist) {
            throw new ProductSkuAlreadyExistsException("Product with " + request.getSku() + " sku already exists");
        }
        Product product = modelMapper.map(request, Product.class);
        product.setStatus(ProductStatus.ACTIVE);
        Product savedProduct = productRepository.save(product);

        ProductCreatedEvent productCreatedEvent = modelMapper.map(savedProduct, ProductCreatedEvent.class);
        productCreatedEvent.setTotalQuantity(request.getTotalQuantity());

        kafkaTemplate.send(KAFKA_PRODUCT_CREATED_TOPIC, productCreatedEvent.getSku(), productCreatedEvent);

        return modelMapper.map(savedProduct, ProductResponseDTO.class);
    }

    @Modifying
    public ProductResponseDTO update(Long productId, UpdateProductRequestDTO request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        modelMapper.map(request, product);

        Product updatedProduct = productRepository.save(product);
        return modelMapper.map(updatedProduct, ProductResponseDTO.class);
    }

    public List<ProductResponseDTO> findAllByIds(List<Long> ids) {
        List<Product> products = productRepository.findAllById(ids);
        log.info("Fetching Products for product id: {}", ids);

        return products
                .stream().map(p-> modelMapper.map(p, ProductResponseDTO.class))
                .collect(Collectors.toList());
    }
}