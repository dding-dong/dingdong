package com.sparta.dingdong.domain.cart.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sparta.dingdong.domain.cart.entity.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, UUID> {
	Optional<Cart> findByUserId(Long userId);
}
