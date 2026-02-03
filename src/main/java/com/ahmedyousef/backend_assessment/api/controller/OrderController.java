package com.ahmedyousef.backend_assessment.api.controller;

import com.ahmedyousef.backend_assessment.api.dto.PlaceOrderRequest;
import com.ahmedyousef.backend_assessment.api.dto.PlaceOrderResponse;
import com.ahmedyousef.backend_assessment.application.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<PlaceOrderResponse> placeOrder(@RequestBody PlaceOrderRequest req) {
        return ResponseEntity.ok(orderService.placeOrder(req));
    }

}
