package com.sparta.dingdong.domain.order.dto.response;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MasterOrderResponseDto {
	private final UUID orderId;
	private final UUID storeId;
	private final String storeName;
	private final String orderStatus;
	private final BigInteger totalPrice;
	private final String paymentStatus;
	private final String paymentTxId; // 결제 ID
	private final String refundTxId; // 환불 ID
	private final BigInteger refundAmount;
	private final LocalDateTime placedAt;
	private final LocalDateTime deliveredAt;
	private final String deliveryAddress;
	private final List<OrderItemResponseDto> items;
	private final String cancelReason;
	private final String customerName;
	private final String customerPhone;
	private final Long createdBy;
	private final String createdByName;
	private final Long updatedBy;
}
