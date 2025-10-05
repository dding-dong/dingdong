package com.sparta.dingdong.domain.review.service;

import java.util.UUID;

import com.sparta.dingdong.common.jwt.UserAuth;
import com.sparta.dingdong.domain.review.dto.CustomerReviewDto;

public interface CustomerReviewService {
	void createReview(UUID orderId, UserAuth userDetails, CustomerReviewDto.CreateReview request);
}
