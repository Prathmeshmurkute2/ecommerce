package com.ecommerce.controller;


import com.ecommerce.dto.CartItemRequest;
import com.ecommerce.dto.CartItemResponse;
import com.ecommerce.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping
    public ResponseEntity<String> addToCart(
            @RequestHeader("X-User-ID") String userId,
            @RequestBody CartItemRequest request){
        if(!cartService.addToCart(userId, request)){
            return ResponseEntity.badRequest().body("Product out of stock or User not found or product not found");
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/item/{productId}")
    public ResponseEntity<Void> removeFromCart(
            @RequestHeader("X-User-ID") String userId,
            @PathVariable long productId
    ){
        boolean deleted = cartService.deleteItemFrom(userId, productId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();

    }

    @GetMapping("/items")
    public ResponseEntity<List<CartItemResponse>> getCartItems(
            @RequestHeader("X-User-ID") String userId){
        List<CartItemResponse> items = cartService.getCartItems(userId);

        return  ResponseEntity.ok(items);
    }
}
