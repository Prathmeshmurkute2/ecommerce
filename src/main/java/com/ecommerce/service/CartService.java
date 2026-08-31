package com.ecommerce.service;

import com.ecommerce.dto.CartItemRequest;
import com.ecommerce.dto.CartItemResponse;
import com.ecommerce.model.CartItem;
import com.ecommerce.model.Product;
import com.ecommerce.model.User;
import com.ecommerce.repository.CartItemRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;

    public boolean addToCart(String userId, CartItemRequest request) {

        // Validate request
        if (request == null ||
                request.getProductId() == null ||
                request.getQuantity() == null ||
                request.getQuantity() <= 0) {
            return false;
        }

        // Validate user ID
        if (userId == null || userId.isBlank()) {
            return false;
        }

        Long userIdLong;

        try {
            userIdLong = Long.valueOf(userId);
        } catch (NumberFormatException e) {
            return false;
        }

        // Find product
        Optional<Product> productOpt =
                productRepository.findById(request.getProductId());

        if (productOpt.isEmpty()) {
            return false;
        }

        Product product = productOpt.get();

        // Find user
        Optional<User> userOpt =
                userRepository.findById(userIdLong);

        if (userOpt.isEmpty()) {
            return false;
        }

        User user = userOpt.get();

        // Check existing cart item
        CartItem existingCartItem =
                cartItemRepository.findByUserAndProduct(user, product);

        if (existingCartItem != null) {

            int newQuantity =
                    existingCartItem.getQuantity() + request.getQuantity();

            // Check total quantity against stock
            if (newQuantity > product.getStockQuantity()) {
                return false;
            }

            existingCartItem.setQuantity(newQuantity);

            BigDecimal totalPrice =
                    product.getPrice().multiply(BigDecimal.valueOf(newQuantity));

            existingCartItem.setPrice(totalPrice);

            cartItemRepository.save(existingCartItem);

        } else {

            // Check requested quantity against stock
            if (request.getQuantity() > product.getStockQuantity()) {
                return false;
            }

            CartItem cartItem = new CartItem();

            cartItem.setUser(user);
            cartItem.setProduct(product);
            cartItem.setQuantity(request.getQuantity());

            BigDecimal totalPrice =
                    product.getPrice()
                            .multiply(BigDecimal.valueOf(request.getQuantity()));

            cartItem.setPrice(totalPrice);

            cartItemRepository.save(cartItem);
        }

        return true;
    }

    public boolean deleteItemFrom(String userId, long productId) {
        Optional<Product> productOpt = productRepository.findById(productId);
        Optional<User> userOpt =userRepository.findById(Long.valueOf(userId));

        if(productOpt.isPresent() && userOpt.isPresent()) {
            cartItemRepository.deleteByUserAndProduct(userOpt.get(), productOpt.get());
            return true;

        }
        return false;
    }

    public List<CartItemResponse> getCartItems(String userId) {
        if(userId==null || userId.isBlank()){
            return List.of();
        }

        Long userIdLong;

        try{
            userIdLong= Long.valueOf(userId);

        }catch(NumberFormatException e){
            return List.of();
        }

        Optional<User> userOpt= userRepository.findById(userIdLong);

        if(userOpt.isEmpty()){
            return List.of();
        }

        User user= userOpt.get();

        List<CartItem> cartItems = cartItemRepository.findByUser(user);

        return cartItems.stream()
                .map(cartItem -> new CartItemResponse(
                        cartItem.getProduct().getId(),
                        cartItem.getProduct().getName(),
                        cartItem.getPrice(),
                        cartItem.getQuantity()
                ))
                .toList();
    }
}

