package com.sparta.dingdong.domain.order.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderStatusUpdateRequestDto {

	@NotNull
	private final String status;

	private final String cancelReason;

}
