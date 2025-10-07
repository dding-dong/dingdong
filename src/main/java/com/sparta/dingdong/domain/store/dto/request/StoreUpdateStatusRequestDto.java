package com.sparta.dingdong.domain.store.dto.request;

import com.sparta.dingdong.domain.store.entity.enums.StoreStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "매장 상태 변경 요청 DTO")
public class StoreUpdateStatusRequestDto {

	@Schema(
		description = "매장 상태 (예: PREPARING, OPEN, CLOSED, FORCED_CLOSED)",
		example = "OPEN",
		implementation = StoreStatus.class
	)
	private StoreStatus status;
}