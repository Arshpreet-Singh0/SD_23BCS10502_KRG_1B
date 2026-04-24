package com.ecommerce.order_service.services;

import com.ecommerce.order_service.clients.InventoryFeignClient;
import com.ecommerce.order_service.clients.ProductFeignClient;
import com.ecommerce.order_service.dto.*;
import com.ecommerce.order_service.entities.Order;
import com.ecommerce.order_service.entities.OrderItem;
import com.ecommerce.order_service.entities.OrderStatus;
import com.ecommerce.order_service.exceptions.InventoryReservationException;
import com.ecommerce.order_service.exceptions.PriceMismatchException;
import com.ecommerce.order_service.exceptions.ProductNotFoundException;
import com.ecommerce.order_service.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductFeignClient productFeignClient;
    private final InventoryFeignClient inventoryFeignClient;
    private final ModelMapper modelMapper;

    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO dto) {
        log.info("OrderService: createOrder {}", dto);

        // 1. Idempotency check
        Order existing = getExistingOrder(dto.getIdempotencyKey());
        Map<Long, ProductDTO> productMap = fetchAndValidateProducts(dto);
        log.info("Existing {}", existing);
        log.info("OrderService: createOrder {}", productMap);

        if (existing != null) {

            if (existing.getStatus() == OrderStatus.FAILED) {
                // retry inventory
                reserveInventory(existing, dto, productMap);
                existing.setStatus(OrderStatus.PAYMENT_PENDING);
                orderRepository.save(existing);
                return mapToResponse(existing);
            }

            return mapToResponse(existing);
        }

        // 2. Fetch & validate products

        // 3. Build order
        Order order = buildOrder(dto, productMap);

        log.info("Order {}", order.toString());

        // 4. Save order
        Order savedOrder = orderRepository.save(order);

        // 5. Reserve inventory
        reserveInventory(savedOrder, dto, productMap);

        log.info("OrderService: createOrder {}", savedOrder);

        return mapToResponse(savedOrder);
    }

    // ---------------- HELPERS ----------------

    private Order getExistingOrder(String idempotencyKey) {
        if (idempotencyKey == null) return null;
        return orderRepository.findByIdempotencyKey(idempotencyKey).orElse(null);
    }

    private Map<Long, ProductDTO> fetchAndValidateProducts(OrderRequestDTO dto) {

        List<Long> productIds = dto.getItems().stream()
                .map(OrderItemDTO::getProductId)
                .toList();

        List<ProductDTO> products = productFeignClient.getProductsByIds(productIds).getData();

        if (products.size() != productIds.size()) {
            throw new ProductNotFoundException("Some products are missing");
        }

        return products.stream()
                .collect(Collectors.toMap(ProductDTO::getId, p -> p));
    }

    private Order buildOrder(OrderRequestDTO dto, Map<Long, ProductDTO> productMap) {

        Long userId = (Long) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        Order order = new Order();
        order.setUserId(userId);
        order.setStatus(OrderStatus.PAYMENT_PENDING);
        order.setIdempotencyKey(dto.getIdempotencyKey());

        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemDTO itemDTO : dto.getItems()) {

            ProductDTO product = productMap.get(itemDTO.getProductId());

            BigDecimal finalPrice = calculateFinalPrice(product);

            validatePrice(itemDTO, finalPrice);

            OrderItem item = new OrderItem();
            item.setProductId(itemDTO.getProductId());
            item.setQuantity(itemDTO.getQuantity());
            item.setPrice(finalPrice);

            order.addItem(item);

            total = total.add(
                    finalPrice.multiply(BigDecimal.valueOf(itemDTO.getQuantity()))
            );
        }

        order.setTotalAmount(total);
        return order;
    }

    private BigDecimal calculateFinalPrice(ProductDTO product) {
        BigDecimal price = product.getPrice();

        if (product.getDiscountPercent() == null) return price;

        BigDecimal discount = price.multiply(product.getDiscountPercent())
                .divide(BigDecimal.valueOf(100));

        return price.subtract(discount);
    }

    private void validatePrice(OrderItemDTO itemDTO, BigDecimal finalPrice) {
        if (itemDTO.getPrice() != null &&
                itemDTO.getPrice().compareTo(finalPrice) != 0) {
            throw new PriceMismatchException("Price mismatch for product: " + itemDTO.getProductId());
        }
    }

    private void reserveInventory(Order order,
                                  OrderRequestDTO dto,
                                  Map<Long, ProductDTO> productMap) {

        ReserveInventoryRequest reserveDTO = new ReserveInventoryRequest();
        reserveDTO.setOrderId(order.getId().toString());

        List<ReserveInventoryRequest.Item> items = dto.getItems().stream()
                .collect(Collectors.groupingBy(
                        item -> productMap.get(item.getProductId()).getSku(),
                        Collectors.summingInt(OrderItemDTO::getQuantity)
                ))
                .entrySet()
                .stream()
                .map(entry -> {
                    ReserveInventoryRequest.Item i = new ReserveInventoryRequest.Item();
                    i.setSku(entry.getKey());
                    i.setQty(entry.getValue());
                    return i;
                })
                .toList();

        reserveDTO.setItems(items);

        try {
            inventoryFeignClient.reserve(reserveDTO);
        } catch (Exception e) {
            order.setStatus(OrderStatus.FAILED);
            orderRepository.save(order);
            throw new InventoryReservationException("Inventory reservation failed");
        }
    }

    private OrderResponseDTO mapToResponse(Order order) {
        return modelMapper.map(order, OrderResponseDTO.class);
    }
}