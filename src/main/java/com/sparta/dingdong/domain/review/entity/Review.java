package com.sparta.dingdong.domain.review.entity;

import java.util.UUID;

import com.sparta.dingdong.common.base.BaseEntity;
import com.sparta.dingdong.domain.order.entity.Order;
import com.sparta.dingdong.domain.review.dto.CustomerReviewDto;
import com.sparta.dingdong.domain.store.entity.Store;
import com.sparta.dingdong.domain.user.entity.User;

import jakarta.persistence.CascadeType;
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
@Table(name = "p_review")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Review extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id")
	private Order order;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "store_id")
	private Store store;

	private Integer rating;

	private String content;

	private String imageUrl1;

	private String imageUrl2;

	private String imageUrl3;

	private boolean isDisplayed;

	@OneToOne(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
	private ReviewReply reviewReply;

	public static Review create(User user, Order order, CustomerReviewDto.CreateReview request) {
		return Review.builder()
			.user(user)
			.order(order)
			.store(order.getStore())
			.rating(request.getRating())
			.content(request.getContent())
			.imageUrl1(request.getImageUrl1())
			.imageUrl2(request.getImageUrl2())
			.imageUrl3(request.getImageUrl3())
			.isDisplayed(request.isDisplayed())
			.build();
	}

	public void updateReview(CustomerReviewDto.UpdateReview request) {
		this.rating = request.getRating() != null ? request.getRating() : this.rating;
		this.content = request.getContent() != null ? request.getContent() : this.content;
		this.imageUrl1 = request.getImageUrl1() != null ? request.getImageUrl1() : this.imageUrl1;
		this.imageUrl2 = request.getImageUrl2() != null ? request.getImageUrl2() : this.imageUrl2;
		this.imageUrl3 = request.getImageUrl3() != null ? request.getImageUrl3() : this.imageUrl3;
		this.isDisplayed = request.isDisplayed();
	}

	public void deleteReview(User user) {
		softDeleteBy(user.getId());
		softDelete();
	}

	public void reactivate(User user, CustomerReviewDto.CreateReview request) {
		this.user = user;
		this.rating = request.getRating();
		this.content = request.getContent();
		this.imageUrl1 = request.getImageUrl1();
		this.imageUrl2 = request.getImageUrl2();
		this.imageUrl3 = request.getImageUrl3();
		this.isDisplayed = request.isDisplayed();
		reactivate();
	}

	public boolean isActive() {
		return this.getDeletedAt() == null && this.getDeletedBy() == null && this.isDisplayed();
	}

	public void hide() {
		this.isDisplayed = false;
	}
}
