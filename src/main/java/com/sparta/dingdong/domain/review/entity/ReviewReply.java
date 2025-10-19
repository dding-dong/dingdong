package com.sparta.dingdong.domain.review.entity;

import java.util.UUID;

import com.sparta.dingdong.common.base.BaseEntity;
import com.sparta.dingdong.domain.order.entity.Order;
import com.sparta.dingdong.domain.review.dto.request.OwnerCreateReplyRequestDto;
import com.sparta.dingdong.domain.review.dto.request.OwnerUpdateReplyRequestDto;
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

	public static ReviewReply createReviewReply(Review review, User user, OwnerCreateReplyRequestDto request) {
		return ReviewReply.builder()
			.review(review)
			.owner(user)
			.order(review.getOrder())
			.store(review.getStore())
			.content(request.getContent())
			.isDisplayed(request.isDisplayed())
			.build();
	}

	public void updateReply(OwnerUpdateReplyRequestDto request) {
		this.content = request.getContent() != null ? request.getContent() : this.content;
		this.isDisplayed = request.isDisplayed();
	}

	public void deleteReply(User user) {
		softDelete(user.getId());
	}

	public void reactivate(Review review, User user, OwnerCreateReplyRequestDto request) {
		this.review = review;
		this.owner = user;
		this.order = review.getOrder();
		this.store = review.getStore();
		this.content = request.getContent();
		this.isDisplayed = request.isDisplayed();
		reactivate();
	}

	public boolean isActive() {
		return this.getDeletedAt() == null && this.getDeletedBy() == null && this.isDisplayed();
	}
}
