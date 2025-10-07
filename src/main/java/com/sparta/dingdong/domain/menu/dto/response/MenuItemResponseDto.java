package com.sparta.dingdong.domain.menu.dto.response;

import java.math.BigInteger;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "메뉴 아이템 응답 DTO")
public class MenuItemResponseDto {

	@Schema(example = "a1b2c3d4-e5f6-7890-abcd-123456789012", description = "메뉴 아이템 UUID")
	private UUID id;

	@Schema(example = "b2c3d4e5-f6a7-8901-abcd-234567890123", description = "매장 UUID")
	private UUID storeId;

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

	@Schema(example = "true", description = "AI 자동 설명 사용 여부")
	private Boolean isAiUsed;

	@Schema(example = "이 메뉴는 불고기와 신선한 야채로 조화로운 맛을 자랑합니다.", description = "AI가 생성한 자동 설명")
	private String aiContent;
}