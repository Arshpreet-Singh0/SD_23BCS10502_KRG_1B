package com.ecommerce.inventory_service.entities;

import com.ecommerce.inventory_service.entities.enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sku;

    private Integer quantity;

    private String orderId;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDateTime expiresAt;
}
