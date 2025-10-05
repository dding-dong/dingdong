package com.sparta.dingdong.domain.cart.dto.response;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CartConflictResponseDto {
	private final UUID existingCartId;
	private final UUID existingStoreId;
	private final String existingStoreName;
	private final UUID newStoreId;
	private final String newStoreName;
}
