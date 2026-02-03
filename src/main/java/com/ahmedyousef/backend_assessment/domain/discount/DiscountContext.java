package com.ahmedyousef.backend_assessment.domain.discount;

import com.ahmedyousef.backend_assessment.domain.enums.UserRole;

import java.math.BigDecimal;

public record DiscountContext(
        UserRole role,
        BigDecimal subtotal
) {
}
