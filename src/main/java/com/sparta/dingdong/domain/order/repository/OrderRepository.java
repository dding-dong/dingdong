package com.sparta.dingdong.domain.order.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.dingdong.domain.order.entity.Order;

public interface OrderRepository extends JpaRepository<Order, UUID> {
}
