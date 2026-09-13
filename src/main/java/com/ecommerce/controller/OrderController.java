package com.ecommerce.controller;

import com.ecommerce.dto.OrderResponse;
import com.ecommerce.security.CustomUserDetails;
import com.ecommerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @AuthenticationPrincipal CustomUserDetails principal) {
        String userId = String.valueOf(principal.getUser().getId());
        return orderService.createOrder(userId)
                .map(orderResponse -> new ResponseEntity<>(orderResponse, HttpStatus.CREATED))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @GetMapping("/my")
    public ResponseEntity<List<OrderResponse>>getMyOrders(
            @AuthenticationPrincipal CustomUserDetails principal){
        String userId = String.valueOf(principal.getUser().getId());
        return ResponseEntity.ok(orderService.getOrdersForUser(userId));
    }
}