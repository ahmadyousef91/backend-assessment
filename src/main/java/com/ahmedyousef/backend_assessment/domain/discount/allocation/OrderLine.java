package com.ahmedyousef.backend_assessment.domain.discount.allocation;

import java.math.BigDecimal;

public record OrderLine(Long key, BigDecimal lineSubtotal) {
}

