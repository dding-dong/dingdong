package com.sparta.dingdong.domain.review.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.dingdong.common.jwt.UserAuth;
import com.sparta.dingdong.domain.order.entity.Order;
import com.sparta.dingdong.domain.order.service.OrderService;
import com.sparta.dingdong.domain.review.dto.CustomerReviewDto;
import com.sparta.dingdong.domain.review.entity.Review;
import com.sparta.dingdong.domain.review.repository.ReviewRepository;
import com.sparta.dingdong.domain.user.entity.User;
import com.sparta.dingdong.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerReviewServiceImpl implements CustomerReviewService {

	private final UserService userService;
	private final OrderService orderService;
	private final ReviewRepository reviewRepository;

	@Override
	@Transactional
	public void createReview(UUID orderId, UserAuth userDetails, CustomerReviewDto.CreateReview request) {
		User user = userService.findByUser(userDetails);

		Order order = orderService.findByOrder(orderId);

		Review review = Review.create(user, order, request);

		reviewRepository.save(review);
	}
}
