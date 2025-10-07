package com.sparta.dingdong.domain.store.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sparta.dingdong.domain.store.entity.enums.StoreStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL) // null 필드는 응답에서 제외
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "매장 응답 DTO")
public class StoreResponseDto {

	@Schema(example = "a1b2c3d4-e5f6-7890-abcd-123456789012", description = "매장 UUID")
	private UUID id;

	@Schema(example = "김밥천국", description = "매장 이름")
	private String name;

	@Schema(example = "분식 전문점", description = "매장 설명")
	private String description;

	@Schema(example = "서울특별시 강남구 테헤란로 123", description = "매장 주소")
	private String address;

	@Schema(example = "06234", description = "우편번호")
	private String postalCode;

	@Schema(example = "OPEN", description = "매장 상태 (예: OPEN, CLOSED 등)", implementation = StoreStatus.class)
	private StoreStatus status;

	@Schema(example = "abb3b4cf-c378-4c55-b7da-123456789012", description = "카테고리 UUID")
	private UUID storeCategoryId;

	@Schema(example = "1001", description = "매장 소유자 ID")
	private Long ownerId;

	@Schema(example = "[\"강남구\", \"서초구\"]", description = "배달 가능 지역 목록")
	private List<String> deliveryAreaIds;

	// 어드민 전용
	@Schema(example = "1", description = "매장 생성자 ID (관리자용)")
	private Long createdBy;

	@Schema(example = "2025-10-07T14:30:00", description = "매장 생성일시")
	private LocalDateTime createdAt;

	@Schema(example = "2", description = "마지막 수정자 ID (관리자용)")
	private Long updatedBy;

	@Schema(example = "2025-10-07T15:00:00", description = "마지막 수정일시")
	private LocalDateTime updatedAt;

	@Schema(example = "2", description = "삭제 처리자 ID (관리자용)")
	private Long deletedBy;

	@Schema(example = "2025-10-07T16:00:00", description = "삭제일시 (소프트 삭제 시 표시)")
	private LocalDateTime deletedAt;
}