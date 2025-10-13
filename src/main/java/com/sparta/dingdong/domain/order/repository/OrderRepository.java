package com.sparta.dingdong.domain.order.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sparta.dingdong.domain.order.entity.Order;
import com.sparta.dingdong.domain.order.entity.enums.OrderStatus;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

	@EntityGraph(attributePaths = {"store"})
	List<Order> findAllByUserId(Long userId); //Long

	@EntityGraph(attributePaths = {"user"})
	List<Order> findAllByStoreId(UUID storeId);

	@EntityGraph(attributePaths = {"store", "orderItems", "orderItems.menuItem"})
	Order findWithDetailsById(UUID orderId);

	List<Order> findAllByUserIdAndStatus(Long userId, OrderStatus status);
}

