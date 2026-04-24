package com.ecommerce.product_service.controllers;

import com.ecommerce.product_service.dto.CreateProductRequestDTO;
import com.ecommerce.product_service.dto.ProductResponseDTO;
import com.ecommerce.product_service.services.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
@Slf4j
public class ProductController {
    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(@RequestBody @Valid CreateProductRequestDTO createProductRequestDTO) {
        log.info("REST request to save Product : {}", createProductRequestDTO);
        ProductResponseDTO productResponseDTO = productService.create(createProductRequestDTO);
        return new ResponseEntity<>(productResponseDTO, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<ProductResponseDTO> updateProduct(@RequestBody @Valid CreateProductRequestDTO createProductRequestDTO) {
        ProductResponseDTO productResponseDTO = productService.create(createProductRequestDTO);
        return new ResponseEntity<>(productResponseDTO, HttpStatus.OK);
    }

    @PostMapping("/get")
    public ResponseEntity<List<ProductResponseDTO>> findAllProducts(@RequestBody List<Long> ids) {
        List<ProductResponseDTO> products = productService.findAllByIds(ids);

        return ResponseEntity.ok(products);
    }
}
