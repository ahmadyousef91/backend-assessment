package com.ahmedyousef.backend_assessment.api.dto;

import java.util.List;

public record PlaceOrderRequest(
        Long userId,
        List<Item> items
) {
    public record Item(Long productId, int quantity) {}
}
