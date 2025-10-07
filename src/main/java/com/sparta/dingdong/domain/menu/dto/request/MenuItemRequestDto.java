package com.sparta.dingdong.domain.menu.dto.request;

import java.math.BigInteger;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItemRequestDto {
	private String name;
	private String content;
	private BigInteger price;
	private String imageUrl;
	private Boolean isRecommended;
	private Boolean isDisplayed;
	private Boolean isSoldout;
	private Boolean useAi; // AI 자동 설명 생성 여부
}