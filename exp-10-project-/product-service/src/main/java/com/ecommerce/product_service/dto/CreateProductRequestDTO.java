package com.ecommerce.product_service.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProductRequestDTO {

    @NotBlank
    private String name;

    @NotBlank
    @Size(max = 5000)
    private String description;

    @NotBlank
    @Pattern(regexp = "^[A-Z0-9-_]+$")
    private String sku;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal price;

    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private BigDecimal discountPercent;

    @NotBlank
    @Pattern(regexp = "^[A-Z]{3}$")
    private String currency;

    @NotBlank
    private String category;

    @NotBlank
    private String brand;

    @NotNull(message = "totalQuantity is required")
    private Integer totalQuantity;

    @NotEmpty
    private List<@NotBlank String> imageUrls;
}
