package com.sparta.dingdong.domain.order.repository;

import com.sparta.dingdong.domain.order.entity.OrderStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory, UUID> {
    List<OrderStatusHistory> findAllByOrderIdOrderByChangedAtAsc(UUID orderId);
}
