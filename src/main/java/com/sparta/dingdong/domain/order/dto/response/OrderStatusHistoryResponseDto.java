package com.sparta.dingdong.domain.order.dto.response;

import java.time.LocalDateTime;

import com.sparta.dingdong.domain.order.entity.OrderStatusHistory;
import com.sparta.dingdong.domain.order.entity.enums.OrderStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderStatusHistoryResponseDto {

	private OrderStatus status;
	private LocalDateTime changedAt;
	private Long changedBy;

	public static OrderStatusHistoryResponseDto from(OrderStatusHistory history) {
		return OrderStatusHistoryResponseDto.builder()
			.status(history.getStatus())
			.changedAt(history.getChangedAt())
			.changedBy(history.getCreatedBy()) // 기록한 사용자
			.build();
	}
}
