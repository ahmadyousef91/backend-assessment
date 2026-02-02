package com.ahmedyousef.backend_assessment.api.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank @Size(max = 255)
        String name,

        @Size(max = 2000)
        String description,

        @NotNull @DecimalMin(value = "0.01")
        BigDecimal price,

        @Min(0)
        int quantity
) {}