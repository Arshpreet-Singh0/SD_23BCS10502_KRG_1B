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
public class UpdateProductRequestDTO {

    @NotBlank
    private String name;

    @Size(max = 5000)
    private String description;

    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal price;

    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private BigDecimal discountPercent;

    @Pattern(regexp = "^[A-Z]{3}$")
    private String currency;

    @NotBlank
    private String category;

    @NotBlank
    private String brand;

    private List<@NotBlank String> imageUrls;

    @NotNull
    private Boolean isVisible;
}
