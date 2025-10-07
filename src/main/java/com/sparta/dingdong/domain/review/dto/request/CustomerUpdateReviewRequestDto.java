package com.sparta.dingdong.domain.review.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class CustomerUpdateReviewRequestDto {
	@Schema(description = "별점 (1~5)", example = "5")
	private Integer rating;

	@Schema(description = "리뷰 내용", example = "맛있어요!")
	private String content;

	@Schema(description = "이미지 URL 1", example = "https://example.com/image1.jpg")
	private String imageUrl1;

	@Schema(description = "이미지 URL 2", example = "https://example.com/image2.jpg")
	private String imageUrl2;

	@Schema(description = "이미지 URL 3", example = "https://example.com/image3.jpg")
	private String imageUrl3;

	@Schema(description = "리뷰 노출 여부")
	private boolean isDisplayed;
}
