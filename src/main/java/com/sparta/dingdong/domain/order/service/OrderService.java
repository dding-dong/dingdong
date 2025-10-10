package com.sparta.dingdong.domain.order.service;

import java.util.List;
import java.util.UUID;

import com.sparta.dingdong.domain.order.entity.Order;

public interface OrderService {

	Order findByOrder(UUID orderId);

	List<Order> findByOrders(Long userId);
}
