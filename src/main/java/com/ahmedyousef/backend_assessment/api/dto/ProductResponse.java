package com.ahmedyousef.backend_assessment.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        int quantity,
        boolean available,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}