package com.ahmedyousef.backend_assessment.application.product;

import com.ahmedyousef.backend_assessment.domain.entity.Product;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

@NoArgsConstructor
public final class ProductSpecs {

    public static Specification<Product> notDeleted() {

        return (root, q, cb) -> cb.isFalse(root.get("deleted"));
    }

    public static Specification<Product> nameContains(String name) {
        if (name == null || name.isBlank()) {
            return Specification.unrestricted();
        }

        String like = "%" + name.trim().toLowerCase() + "%";
        return (root, q, cb) -> cb.like(cb.lower(root.get("name")), like);
    }

    public static Specification<Product> minPrice(BigDecimal min) {
        if (min == null) {
            return Specification.unrestricted();
        }
        return (root, q, cb) -> cb.greaterThanOrEqualTo(root.get("price"), min);
    }

    public static Specification<Product> maxPrice(BigDecimal max) {
        if (max == null) {
            return Specification.unrestricted();
        }
        return (root, q, cb) -> cb.lessThanOrEqualTo(root.get("price"), max);
    }

    public static Specification<Product> availability(Boolean available) {
        if (available == null) {
            return Specification.unrestricted();
        }

        return (root, q, cb) ->
                available ? cb.gt(root.get("quantity"), 0) : cb.le(root.get("quantity"), 0);
    }   
}
