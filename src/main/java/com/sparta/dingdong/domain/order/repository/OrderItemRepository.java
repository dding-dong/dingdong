package com.sparta.dingdong.domain.order.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sparta.dingdong.domain.order.entity.OrderItem;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {

	List<OrderItem> findAllByOrderId(UUID orderId);

	List<OrderItem> findAllByMenuItemId(UUID menuItemId);
}
