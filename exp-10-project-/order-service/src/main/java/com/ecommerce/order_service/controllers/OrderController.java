package com.ecommerce.order_service.controllers;

import com.ecommerce.order_service.dto.OrderRequestDTO;
import com.ecommerce.order_service.dto.OrderResponseDTO;
import com.ecommerce.order_service.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@RequestBody OrderRequestDTO dto){
        OrderResponseDTO orderResponseDTO = orderService.createOrder(dto);
        return ResponseEntity.ok(orderResponseDTO);
    }
}
