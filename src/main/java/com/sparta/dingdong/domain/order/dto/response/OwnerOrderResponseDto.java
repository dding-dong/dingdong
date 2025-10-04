package com.sparta.dingdong.domain.order.dto.response;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OwnerOrderResponseDto {
	private final UUID orderId;
	private final UUID storeId;
	private final String storeName;
	private final String orderStatus;
	private final BigInteger totalPrice;
	private final String paymentStatus;
	private final LocalDateTime placedAt;
	private final LocalDateTime deliveredAt;
	private final String deliveryAddress;
	private final List<OrderItemResponseDto> items;
	private final String cancelReason;
	private final String customerName;
	private final String customerPhone;
}
