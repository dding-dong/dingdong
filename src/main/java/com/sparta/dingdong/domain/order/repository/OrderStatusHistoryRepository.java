package com.sparta.dingdong.domain.order.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.dingdong.domain.order.entity.OrderStatusHistory;

public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory, UUID> {
	List<OrderStatusHistory> findAllByOrderIdOrderByChangedAtAsc(UUID orderId);
}
