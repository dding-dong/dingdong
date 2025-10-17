package com.sparta.dingdong.domain.review.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.sparta.dingdong.common.TestJasyptConfig;
import com.sparta.dingdong.common.jwt.UserAuth;
import com.sparta.dingdong.domain.order.entity.Order;
import com.sparta.dingdong.domain.order.service.OrderService;
import com.sparta.dingdong.domain.review.dto.request.CustomerCreateReviewRequestDto;
import com.sparta.dingdong.domain.review.entity.Review;
import com.sparta.dingdong.domain.review.exception.OrderAlreadyReviewedException;
import com.sparta.dingdong.domain.review.repository.ReviewQueryRepository;
import com.sparta.dingdong.domain.review.repository.ReviewReplyRepository;
import com.sparta.dingdong.domain.review.repository.ReviewRepository;
import com.sparta.dingdong.domain.store.entity.Store;
import com.sparta.dingdong.domain.user.entity.User;
import com.sparta.dingdong.domain.user.entity.enums.UserRole;
import com.sparta.dingdong.domain.user.service.UserService;

@SpringBootTest(classes = {CustomerReviewServiceImpl.class, TestJasyptConfig.class})
class CustomerReviewServiceImplTest {

	@Autowired
	CustomerReviewService customerReviewService;

	@MockBean
	OrderService orderService;

	@MockBean
	UserService userService;

	@MockBean
	ReviewRepository reviewRepository;

	@MockBean
	ReviewReplyRepository reviewReplyRepository;

	@MockBean
	ReviewQueryRepository reviewQueryRepository;

	@DisplayName("정상: 새로운 리뷰를 생성한다")
	@Test
	void createReview_success() {

		// given
		User customer = User.builder()
			.id(1L)
			.username("exampleName")
			.nickname("exampleName")
			.phone("010-9999-9999")
			.email("customere@example.com")
			.password("Password123!")
			.userRole(UserRole.CUSTOMER)
			.build();

		User owner = User.builder()
			.id(2L)
			.username("exampleName")
			.nickname("exampleName")
			.phone("010-9999-9999")
			.email("owner@example.com")
			.password("Password123!")
			.userRole(UserRole.OWNER)
			.build();

		Order order = Order.builder()
			.id(UUID.randomUUID())
			.user(customer)
			.store(Store.builder()
				.id(UUID.randomUUID())
				.owner(owner)
				.name("exampleStoreName")
				.build())
			.build();

		UserAuth userAuth = new UserAuth(customer.getId(), customer.getUserRole(), 1L);

		when(userService.findByUser(any(UserAuth.class))).thenReturn(customer);
		when(orderService.findByOrder(any(UUID.class))).thenReturn(order);
		when(reviewRepository.findByOrder(order)).thenReturn(Optional.empty());

		CustomerCreateReviewRequestDto request = CustomerCreateReviewRequestDto.builder()
			.content("맛있어요")
			.imageUrl1("exampleImageUrl1")
			.imageUrl2("exampleImageUrl2")
			.imageUrl3("exampleImageUrl3")
			.isDisplayed(true)
			.rating(5)
			.build();

		// when
		customerReviewService.createReview(order.getId(), userAuth, request);

		// then: ArgumentCaptor로 리뷰 객체 검증
		ArgumentCaptor<Review> reviewCaptor = ArgumentCaptor.forClass(Review.class);
		verify(reviewRepository, times(1)).save(reviewCaptor.capture());

		Review savedReview = reviewCaptor.getValue();

		assertEquals(customer, savedReview.getUser());
		assertEquals(order, savedReview.getOrder());
		assertEquals(request.getContent(), savedReview.getContent());
		assertEquals(request.getImageUrl1(), savedReview.getImageUrl1());
		assertEquals(request.getImageUrl2(), savedReview.getImageUrl2());
		assertEquals(request.getImageUrl3(), savedReview.getImageUrl3());
		assertEquals(request.getRating(), savedReview.getRating());
		assertTrue(savedReview.isActive());
	}

	@DisplayName("예외: 이미 활성화된 리뷰가 있으면 OrderAlreadyReviewedException 발생")
	@Test
	void createReview_alreadyReviewed_throwsException() {

		// given
		User customer = User.builder()
			.id(1L)
			.userRole(UserRole.CUSTOMER)
			.build();

		Order order = Order.builder()
			.id(UUID.randomUUID())
			.user(customer)
			.build();

		CustomerCreateReviewRequestDto request = CustomerCreateReviewRequestDto.builder()
			.content("맛있어요")
			.imageUrl1("exampleImageUrl1")
			.imageUrl2("exampleImageUrl2")
			.imageUrl3("exampleImageUrl3")
			.isDisplayed(true)
			.rating(5)
			.build();

		UserAuth userAuth = new UserAuth(customer.getId(), customer.getUserRole(), 1L);

		when(userService.findByUser(any(UserAuth.class))).thenReturn(customer);
		when(orderService.findByOrder(any())).thenReturn(order);

		// Spy를 사용하여 실제 엔티티 + 호출 검증 가능
		Review activeReview = spy(Review.builder()
			.content("기존 내용")
			.rating(3)
			.isDisplayed(true)
			.build());

		when(reviewRepository.findByOrder(order)).thenReturn(Optional.of(activeReview));

		// when & then
		assertThrows(
			OrderAlreadyReviewedException.class,
			() -> customerReviewService.createReview(order.getId(), userAuth, request)
		);

		// reviewRepository.save는 호출되지 않아야 함
		verify(reviewRepository, never()).save(any());
	}

	@DisplayName("기존 리뷰가 삭제/숨김 상태이면 재활성화")
	@Test
	void handleExistingReview_inactiveReview_reactivates() {

		// given: 고객, 주문
		User customer = User.builder()
			.id(1L)
			.userRole(UserRole.CUSTOMER)
			.build();

		Order order = Order.builder()
			.id(UUID.randomUUID())
			.user(customer)
			.build();

		UserAuth userAuth = new UserAuth(customer.getId(), customer.getUserRole(), 1L);

		CustomerCreateReviewRequestDto request = CustomerCreateReviewRequestDto.builder()
			.content("맛있어요")
			.imageUrl1("exampleImageUrl1")
			.imageUrl2("exampleImageUrl2")
			.imageUrl3("exampleImageUrl3")
			.isDisplayed(true)
			.rating(5)
			.build();

		// order에 해당하는 리뷰가 존재
		when(userService.findByUser(any(UserAuth.class))).thenReturn(customer);
		when(orderService.findByOrder(any(UUID.class))).thenReturn(order);

		// Spy를 사용하여 실제 엔티티 + 호출 검증 가능
		Review inactiveReview = spy(Review.builder()
			.content("기존 내용")
			.rating(3)
			.isDisplayed(false)
			.build());

		when(reviewRepository.findByOrder(order)).thenReturn(Optional.of(inactiveReview));

		// when
		customerReviewService.createReview(order.getId(), userAuth, request);

		// then
		verify(inactiveReview, times(1)).reactivate(customer, request); // 호출 확인
		assertTrue(inactiveReview.isActive()); // 상태 확인
		assertEquals(request.getContent(), inactiveReview.getContent());
		assertEquals(request.getRating(), inactiveReview.getRating()); // reactivate 호출 확인
	}
}
