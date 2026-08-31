package com.ecommerce.service;

import com.ecommerce.dto.OrderItemDTO;
import com.ecommerce.dto.OrderResponse;
import com.ecommerce.model.CartItem;
import com.ecommerce.model.Order;
import com.ecommerce.model.OrderItem;
import com.ecommerce.model.OrderStatus;
import com.ecommerce.model.User;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final CartService cartService;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    public Optional<OrderResponse> createOrder(String userId) {

        // Validate user ID
        if (userId == null || userId.isBlank()) {
            return Optional.empty();
        }

        Long userIdLong;

        try {
            userIdLong = Long.valueOf(userId);
        } catch (NumberFormatException e) {
            return Optional.empty();
        }

        // Get cart items
        List<CartItem> cartItems =
                cartService.getCartItemEntities(userId);

        // Validate cart
        if (cartItems.isEmpty()) {
            return Optional.empty();
        }

        // Find user
        Optional<User> userOptional =
                userRepository.findById(userIdLong);

        if (userOptional.isEmpty()) {
            return Optional.empty();
        }

        User user = userOptional.get();

        // Calculate total price
        BigDecimal totalPrice = cartItems.stream()
                .map(CartItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Create order
        Order order = new Order();

        order.setUser(user);
        order.setStatus(OrderStatus.CONFIRMED);
        order.setTotalAmount(totalPrice);

        // Create order items
        List<OrderItem> orderItems = cartItems.stream()
                .map(item -> new OrderItem(
                        null,
                        item.getProduct(),
                        item.getQuantity(),
                        item.getPrice(),
                        order
                ))
                .toList();

        // Add items to order
        order.setItems(orderItems);

        // Save order
        Order savedOrder = orderRepository.save(order);

        // Clear cart
        cartService.clearCart(userId);

        // Return response
        return Optional.of(mapToOrderResponse(savedOrder));
    }

    private OrderResponse mapToOrderResponse(Order order) {

        return new OrderResponse(
                order.getId(),
                order.getTotalAmount(),
                order.getStatus(),

                order.getItems().stream()
                        .map(orderItem -> new OrderItemDTO(
                                orderItem.getId(),
                                orderItem.getProduct().getId(),
                                orderItem.getQuantity(),
                                orderItem.getPrice(),
                                orderItem.getPrice()
                                        .multiply(
                                                BigDecimal.valueOf(
                                                        orderItem.getQuantity()
                                                )
                                        )
                        ))
                        .toList(),

                order.getCreatedAt()
        );
    }
}