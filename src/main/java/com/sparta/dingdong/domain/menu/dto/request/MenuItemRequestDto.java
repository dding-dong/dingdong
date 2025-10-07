package com.sparta.dingdong.domain.menu.dto.request;

import java.math.BigInteger;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "메뉴 아이템 등록 요청 DTO")
public class MenuItemRequestDto {

	@Schema(example = "불고기버거 세트", description = "메뉴 이름")
	private String name;

	@Schema(example = "불고기 패티와 신선한 야채로 구성된 인기 세트 메뉴", description = "메뉴 설명")
	private String content;

	@Schema(example = "6500", description = "메뉴 가격 (단위: 원)")
	private BigInteger price;

	@Schema(example = "https://image.url/bulgogi_burger.jpg", description = "메뉴 이미지 URL")
	private String imageUrl;

	@Schema(example = "true", description = "추천 메뉴 여부")
	private Boolean isRecommended;

	@Schema(example = "true", description = "메뉴 노출 여부 (true: 노출, false: 비노출)")
	private Boolean isDisplayed;

	@Schema(example = "false", description = "품절 여부 (true: 품절, false: 판매 중)")
	private Boolean isSoldout;

	@Schema(example = "true", description = "AI 자동 설명 생성 여부")
	private Boolean useAi; // AI 자동 설명 생성 여부
}