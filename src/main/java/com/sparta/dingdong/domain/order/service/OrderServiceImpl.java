package com.sparta.dingdong.domain.order.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.sparta.dingdong.domain.order.entity.Order;
import com.sparta.dingdong.domain.order.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

	private final OrderRepository orderRepository;

	@Override
	public Order findByOrder(UUID orderId) {
		return orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("해당하는 Order가 없습니다"));
	}

	@Override
	public List<Order> findByOrders(Long userId) {
		return orderRepository.findAllByUserId(userId);
	}
}
