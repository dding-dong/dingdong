package com.sparta.dingdong.domain.order.dto.response;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.sparta.dingdong.domain.order.entity.Order;
import com.sparta.dingdong.domain.order.entity.enums.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class OrderDetailResponseDto {
	private UUID orderId;
	private String storeName;
	private String deliveryAddress;
	private OrderStatus status;
	private BigInteger totalPrice;
	private LocalDateTime placedAt;
	private LocalDateTime deliveredAt;
	private List<OrderItemResponseDto> orderItems;

	public static OrderDetailResponseDto from(Order order) {
		return OrderDetailResponseDto.builder()
			.orderId(order.getId())
			.storeName(order.getStore().getName())
			.deliveryAddress(order.getDeliveryAddress())
			.status(order.getStatus())
			.totalPrice(order.getTotalPrice())
			.placedAt(order.getPlacedAt())
			.deliveredAt(order.getDeliveredAt())
			.orderItems(order.getOrderItems().stream()
				.map(OrderItemResponseDto::from)
				.collect(Collectors.toList()))
			.build();
	}
}

