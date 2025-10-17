package com.sparta.dingdong.domain.review.service;

import java.util.List;
import java.util.UUID;

import com.sparta.dingdong.common.jwt.UserAuth;
import com.sparta.dingdong.domain.review.dto.request.OwnerCreateReplyRequestDto;
import com.sparta.dingdong.domain.review.dto.request.OwnerUpdateReplyRequestDto;
import com.sparta.dingdong.domain.review.dto.response.OwnerReviewDetailsResponseDto;
import com.sparta.dingdong.domain.review.dto.response.OwnerStoreReviewsDto;

public interface OwnerReviewService {

	void createReply(UUID reviewId, UserAuth userDetails, OwnerCreateReplyRequestDto request);

	void updateReply(UUID reviewId, UUID replyId, UserAuth userDetails, OwnerUpdateReplyRequestDto request);

	void deleteReply(UUID reviewId, UUID replyId, UserAuth userDetails);

	OwnerReviewDetailsResponseDto getReviewDetails(UUID reviewId, UserAuth userDetails);

	List<OwnerStoreReviewsDto> getOwnerReviews(UserAuth userDetails);

	OwnerStoreReviewsDto getOwnerStoreReviews(UserAuth userDetails, UUID storeId);
}
