package com.sparta.dingdong.domain.order.service;

import java.util.List;
import java.util.UUID;

import com.sparta.dingdong.common.jwt.UserAuth;
import com.sparta.dingdong.domain.order.dto.request.CreateOrderRequestDto;
import com.sparta.dingdong.domain.order.dto.request.UpdateOrderStatusRequestDto;
import com.sparta.dingdong.domain.order.dto.response.OrderDetailResponseDto;
import com.sparta.dingdong.domain.order.dto.response.OrderListResponseDto;
import com.sparta.dingdong.domain.order.dto.response.OrderResponseDto;
import com.sparta.dingdong.domain.order.entity.Order;

public interface OrderService {

	OrderResponseDto createOrder(UserAuth userAuth, CreateOrderRequestDto request);

	OrderDetailResponseDto getOrderDetail(UserAuth userAuth, UUID orderId);

	OrderListResponseDto getOrderList(UserAuth userAuth);

	void updateOrderStatus(UUID orderId, UpdateOrderStatusRequestDto request);

	Order cancelOrder(UserAuth userAuth, UUID orderId, String reason);

	Order findByOrder(UUID orderId);

	List<Order> findByOrders(Long userId);
}
