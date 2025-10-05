package com.sparta.dingdong.domain.review.entity;

import java.util.UUID;

import com.sparta.dingdong.common.base.BaseEntity;
import com.sparta.dingdong.domain.order.entity.Order;
import com.sparta.dingdong.domain.review.dto.OwnerReviewDto;
import com.sparta.dingdong.domain.store.entity.Store;
import com.sparta.dingdong.domain.user.entity.User;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_review_reply")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewReply extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "review_id")
	private Review review;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User owner;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id")
	private Order order;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "store_id")
	private Store store;

	private String content;

	private boolean isDisplayed;

	public static ReviewReply createReviewReply(Review review, User user, OwnerReviewDto.CreateReply request) {
		return ReviewReply.builder()
			.review(review)
			.owner(user)
			.order(review.getOrder())
			.store(review.getStore())
			.content(request.getContent())
			.isDisplayed(request.isDisplayed())
			.build();
	}
}
