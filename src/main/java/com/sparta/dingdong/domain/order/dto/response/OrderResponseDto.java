package com.sparta.dingdong.domain.order.dto.response;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

import com.sparta.dingdong.domain.order.entity.Order;
import com.sparta.dingdong.domain.order.entity.enums.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class OrderResponseDto {
	private UUID orderId;
	private UUID storeId;
	private String storeName;
	private OrderStatus status;
	private BigInteger totalPrice;
	private LocalDateTime placedAt;
	private String deliveryAddress;

	public static OrderResponseDto from(Order order) {
		return OrderResponseDto.builder()
			.orderId(order.getId())
			.storeId(order.getStore().getId())
			.storeName(order.getStore().getName())
			.status(order.getStatus())
			.totalPrice(order.getTotalPrice())
			.placedAt(order.getPlacedAt())
			.deliveryAddress(order.getDeliveryAddress())
			.build();
	}
}