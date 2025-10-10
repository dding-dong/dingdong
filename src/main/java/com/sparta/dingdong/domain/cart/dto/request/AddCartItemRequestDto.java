package com.sparta.dingdong.domain.cart.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AddCartItemRequestDto {
	@NotNull
	private final UUID menuItemId;
	@Min(1)
	private final int quantity;
}
