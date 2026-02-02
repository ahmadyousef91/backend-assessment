package com.ahmedyousef.backend_assessment.api.mapper;

import com.ahmedyousef.backend_assessment.api.dto.ProductResponse;
import com.ahmedyousef.backend_assessment.domain.entity.Product;

public final class ProductMapper {
    private ProductMapper() {}

    public static ProductResponse toResponse(Product p) {
        return new ProductResponse(
                p.getId(),
                p.getName(),
                p.getDescription(),
                p.getPrice(),
                p.getQuantity(),
                p.getQuantity() > 0,
                p.getCreatedAt(),
                p.getUpdatedAt()
        );
    }
}