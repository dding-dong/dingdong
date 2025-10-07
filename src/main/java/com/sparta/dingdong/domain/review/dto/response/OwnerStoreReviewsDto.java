package com.sparta.dingdong.domain.review.dto.response;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OwnerStoreReviewsDto {
	private UUID storeId;
	private String storeName;
	private List<OwnerReviewResponseDto> reviews;
}
