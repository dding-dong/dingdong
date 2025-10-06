package com.sparta.dingdong.domain.order.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderCancelRequestDto {
	@NotBlank
	private final String cancelReason;

}
