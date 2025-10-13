package com.sparta.dingdong.domain.review.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class OwnerUpdateReplyRequestDto {
	@Schema(description = "리뷰 답장 내용", example = "감사합니다!")
	private String content;

	@Schema(description = "리뷰 딥장 노출 여부")
	private boolean isDisplayed = true;
}
