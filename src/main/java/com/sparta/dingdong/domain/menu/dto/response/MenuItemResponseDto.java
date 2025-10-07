package com.sparta.dingdong.domain.menu.dto.response;

import java.math.BigInteger;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItemResponseDto {
	private UUID id;
	private UUID storeId;
	private String name;
	private String content;
	private BigInteger price;
	private String imageUrl;
	private Boolean isRecommended;
	private Boolean isDisplayed;
	private Boolean isSoldout;
	private Boolean isAiUsed;
	private String aiContent;
}