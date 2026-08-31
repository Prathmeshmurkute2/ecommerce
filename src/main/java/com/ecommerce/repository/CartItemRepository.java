package com.ecommerce.repository;

import com.ecommerce.model.CartItem;
import com.ecommerce.model.Product;
import com.ecommerce.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    CartItem findByUserAndProduct(User user, Product product);

    List<CartItem> findByUser(User user);

    void deleteByUserAndProduct(User user, Product product);

    void deleteByUser(User user);
}