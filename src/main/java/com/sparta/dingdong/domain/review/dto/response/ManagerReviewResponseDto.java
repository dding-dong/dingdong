package com.sparta.dingdong.domain.review.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.sparta.dingdong.domain.review.entity.Review;
import com.sparta.dingdong.domain.review.repository.vo.ManagerSearchReviewVo;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ManagerReviewResponseDto {
	private UUID reviewId;
	private Long userId;
	private UUID orderId;
	private UUID storeId;
	private Integer rating;
	private String content;
	private String imageUrl1;
	private String imageUrl2;
	private String imageUrl3;
	private ManagerReviewReplyResponseDto reply;
	private Boolean isDisplayed;
	private LocalDateTime createdAt;
	private Long createdBy;
	private LocalDateTime updatedAt;
	private Long updatedBy;
	private LocalDateTime deletedAt;
	private Long deletedBy;

	public static ManagerReviewResponseDto from(Review review) {
		return ManagerReviewResponseDto.builder()
			.reviewId(review.getId())
			.userId(review.getUser().getId())
			.orderId(review.getOrder().getId())
			.storeId(review.getStore().getId())
			.rating(review.getRating())
			.content(review.getContent())
			.imageUrl1(review.getImageUrl1())
			.imageUrl2(review.getImageUrl2())
			.imageUrl3(review.getImageUrl3())
			.reply(ManagerReviewReplyResponseDto.from(review.getReviewReply()))
			.isDisplayed(review.isDisplayed())
			.createdAt(review.getCreatedAt())
			.createdBy(review.getCreatedBy())
			.updatedAt(review.getUpdatedAt())
			.updatedBy(review.getUpdatedBy())
			.deletedAt(review.getDeletedAt())
			.deletedBy(review.getDeletedBy())
			.build();
	}

	public static ManagerReviewResponseDto from(ManagerSearchReviewVo vo) {
		return ManagerReviewResponseDto.builder()
			.reviewId(vo.getReviewId())
			.userId(vo.getUserId())
			.orderId(vo.getOrderId())
			.storeId(vo.getStoreId())
			.rating(vo.getRating())
			.content(vo.getContent())
			.imageUrl1(vo.getImageUrl1())
			.imageUrl2(vo.getImageUrl2())
			.imageUrl3(vo.getImageUrl3())
			.reply(ManagerReviewReplyResponseDto.from(vo.getReplyId(), vo.getOwnerId(), vo.getReplyContent(),
				vo.getIsReplyDisplayed(), vo.getReplyCreatedAt(), vo.getReplyCreatedBy(), vo.getReplyUpdatedAt(),
				vo.getReplyUpdatedBy(), vo.getReplyDeletedAt(), vo.getReplyDeletedBy()))
			.isDisplayed(vo.getIsReviewDisplayed())
			.createdAt(vo.getReviewCreatedAt())
			.createdBy(vo.getReviewCreatedBy())
			.updatedAt(vo.getReviewUpdatedAt())
			.updatedBy(vo.getReviewUpdatedBy())
			.deletedAt(vo.getReviewDeletedAt())
			.deletedBy(vo.getReviewDeletedBy())
			.build();
	}

}
