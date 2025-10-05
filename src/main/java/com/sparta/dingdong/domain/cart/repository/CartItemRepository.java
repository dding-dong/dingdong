package com.sparta.dingdong.domain.cart.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sparta.dingdong.domain.cart.entity.CartItem;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, UUID> {
}
