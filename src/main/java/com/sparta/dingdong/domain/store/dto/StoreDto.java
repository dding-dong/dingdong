package com.sparta.dingdong.domain.store.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sparta.dingdong.domain.store.entity.enums.StoreStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class StoreDto {
	@Data
	@Builder
	public static class Request {
		@NotBlank
		private String name;

		private String description;

		@NotBlank
		private String address;

		@NotBlank
		private String postalCode;

		@NotNull
		private UUID categoryId;

		// 배달가능지역
		private List<String> deliveryAreaIds;
	}

	@Data
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class UpdateStatusRequest {
		private StoreStatus status;
	}

	@JsonInclude(JsonInclude.Include.NON_NULL) // null 필드는 응답에서 제외
	@Data
	@Builder
	public static class Response {
		private UUID id;
		private String name;
		private String description;
		private String address;
		private String postalCode;
		private StoreStatus status;
		private UUID categoryId;
		private Long ownerId;

		// 배달가능지역
		private List<String> deliveryAreaIds;

		// 어드민 전용
		private Long createdBy;
		private LocalDateTime createdAt;
		private Long updatedBy;
		private LocalDateTime updatedAt;
		private Long deletedBy;
		private LocalDateTime deletedAt;
	}
}
