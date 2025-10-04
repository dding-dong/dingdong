package com.sparta.dingdong.domain.order.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderStatusHistoryDto {
	private final UUID historyId;
	private final UUID orderId;
	private final String prevStatus;
	private final String newStatus;
	private final Long changedBy;
	private final String changedByName;
	private final LocalDateTime changedAt;
	private final String reasonDetail;
	private final LocalDateTime createdAt;

}
