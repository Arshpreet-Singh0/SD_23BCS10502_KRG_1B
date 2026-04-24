package com.ecommerce.inventory_service.entities;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "inventory",
        uniqueConstraints = @UniqueConstraint(columnNames = "sku")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String sku;

    @Column(nullable = false)
    private Integer totalQuantity;

    @Column(nullable = false)
    private Integer reservedQuantity;

    @Column(nullable = false)
    private Integer availableQuantity;

    @Version
    @Column(nullable = false)
    private Long version;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    private void calculateAvailability() {
        this.availableQuantity = this.totalQuantity - this.reservedQuantity;
    }

}
