package com.sparta.dingdong.domain.review.service;

import java.util.UUID;

import com.sparta.dingdong.common.jwt.UserAuth;
import com.sparta.dingdong.domain.review.dto.OwnerReviewDto;

public interface OwnerReviewService {

	void createReply(UUID reviewId, UserAuth userDetails, OwnerReviewDto.CreateReply request);
}
