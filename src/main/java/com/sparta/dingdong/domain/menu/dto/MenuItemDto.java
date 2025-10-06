package com.sparta.dingdong.domain.menu.dto;

import java.math.BigInteger;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class MenuItemDto {

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class Request {
		private String name;
		private String content;
		private BigInteger price;
		private String imageUrl;
		private Boolean isRecommended;
		private Boolean isDisplayed;
		private Boolean isSoldout;
		private Boolean useAi; // AI 자동 설명 생성 여부
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class Response {
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
}