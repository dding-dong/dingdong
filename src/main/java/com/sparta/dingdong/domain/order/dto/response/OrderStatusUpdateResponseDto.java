package com.sparta.dingdong.domain.order.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderStatusUpdateResponseDto {
	private final UUID orderId;
	private final String orderStatus;
	private final LocalDateTime updatedAt;
}
