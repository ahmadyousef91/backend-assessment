package com.ahmedyousef.backend_assessment.api.dto;

import java.math.BigDecimal;
import java.util.List;

public record PlaceOrderResponse(
        Long orderId,
        BigDecimal subtotal,
        BigDecimal discountTotal,
        BigDecimal total,
        List<Item> items
) {
    public record Item(
            Long productId,
            int quantity,
            BigDecimal unitPrice,
            BigDecimal discountApplied,
            BigDecimal totalPrice
    ) {
    }
}
