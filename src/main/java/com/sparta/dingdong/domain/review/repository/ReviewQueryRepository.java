package com.sparta.dingdong.domain.review.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.dingdong.domain.review.entity.QReview;
import com.sparta.dingdong.domain.review.entity.QReviewReply;
import com.sparta.dingdong.domain.review.repository.vo.ManagerSearchReviewVo;
import com.sparta.dingdong.domain.review.repository.vo.OwnerReviewWithReplyVo;
import com.sparta.dingdong.domain.review.repository.vo.QManagerSearchReviewVo;
import com.sparta.dingdong.domain.review.repository.vo.QOwnerReviewWithReplyVo;
import com.sparta.dingdong.domain.review.repository.vo.QReviewWithReplyActiveVo;
import com.sparta.dingdong.domain.review.repository.vo.QReviewWithReplyVo;
import com.sparta.dingdong.domain.review.repository.vo.ReviewWithReplyActiveVo;
import com.sparta.dingdong.domain.review.repository.vo.ReviewWithReplyVo;
import com.sparta.dingdong.domain.store.entity.QStore;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReviewQueryRepository {

	private final JPAQueryFactory queryFactory;

	//sqlQuery
	// """
	// SELECT
	// r.id        AS review_id,
	// r.user_id   AS user_id,
	// r.order_id  AS order_id,
	// r.store_id  AS store_id,
	// r.rating,
	// r.content,
	// r.image_url1,
	// r.image_url2,
	// r.image_url3,
	// r.is_displayed,
	// rr.id       AS reply_id,
	// rr.user_id  AS owner_id,
	// rr.content  AS reply_content,
	// rr.is_displayed AS reply_is_displayed
	// FROM p_review r
	// LEFT JOIN p_review_reply rr
	// ON r.id = rr.review_id
	// AND rr.deleted_at IS NULL
	// AND rr.deleted_by IS NULL
	// AND rr.is_displayed = true
	// WHERE r.user_id = ?
	// AND r.deleted_at IS NULL
	// AND r.deleted_by IS NULL
	// """
	public List<ReviewWithReplyVo> findAllActiveReviewsWithReplyByUser(Long userId) {
		QReview review = QReview.review;
		QReviewReply reply = QReviewReply.reviewReply;

		return queryFactory
			.select(new QReviewWithReplyVo(
				review.id,
				review.user.id,
				review.order.id,
				review.store.id,
				review.rating,
				review.content,
				review.imageUrl1,
				review.imageUrl2,
				review.imageUrl3,
				review.isDisplayed,
				reply.id,
				reply.owner.id,
				reply.content,
				reply.isDisplayed
			))
			.from(review)
			.leftJoin(review.reviewReply, reply)
			.on(reply.deletedAt.isNull()
				.and(reply.deletedBy.isNull())
				.and(reply.isDisplayed.isTrue()))
			.where(review.user.id.eq(userId)
				.and(review.deletedAt.isNull())
				.and(review.deletedBy.isNull()))
			.fetch();
	}

	// SQl query
	//"""
	// SELECT
	// r.id AS review_id,
	// r.user_id AS user_id,
	// r.order_id AS order_id,
	// r.store_id AS store_id,
	// r.rating,
	// r.content,
	// r.image_url1,
	// r.image_url2,
	// r.image_url3,
	// rr.id AS reply_id,
	// rr.user_id AS owner_id,
	// rr.content AS reply_content
	// FROM p_review r
	// LEFT JOIN p_review_reply rr
	// ON r.id = rr.review_id
	// AND rr.deleted_at IS NULL
	// AND rr.deleted_by IS NULL
	// AND rr.is_displayed = TRUE
	// WHERE r.deleted_at IS NULL
	// AND r.deleted_by IS NULL
	// AND r.is_displayed = TRUE;
	// """
	public List<ReviewWithReplyActiveVo> findAllActiveReviewWithReply() {
		QReview review = QReview.review;
		QReviewReply reply = QReviewReply.reviewReply;

		return queryFactory
			.select(new QReviewWithReplyActiveVo(
				review.id,
				review.user.id,
				review.order.id,
				review.store.id,
				review.rating,
				review.content,
				review.imageUrl1,
				review.imageUrl2,
				review.imageUrl3,
				reply.id,
				reply.owner.id,
				reply.content
			))
			.from(review)
			.leftJoin(review.reviewReply, reply)
			.on(reply.deletedAt.isNull()
				.and(reply.deletedBy.isNull())
				.and(reply.isDisplayed.isTrue()))
			.where(review.deletedAt.isNull()
				.and(review.deletedBy.isNull())
				.and(review.isDisplayed.isTrue()))
			.fetch();
	}

	public List<OwnerReviewWithReplyVo> findAllActiveReviewsWithReplyByOwner(Long ownerId) {
		QStore store = QStore.store;
		QReview review = QReview.review;
		QReviewReply reply = QReviewReply.reviewReply;

		return queryFactory
			.select(
				new QOwnerReviewWithReplyVo(
					store.id,
					store.name,
					review.id,
					review.user.id,
					review.rating,
					review.content,
					review.imageUrl1,
					review.imageUrl2,
					review.imageUrl3,
					review.isDisplayed,
					reply.id,
					reply.owner.id,
					reply.content,
					reply.isDisplayed
				))
			.from(store)
			.join(store.reviews, review)
			.leftJoin(review.reviewReply, reply)
			.on(reply.deletedAt.isNull()
				.and(reply.deletedBy.isNull()))
			.where(
				store.owner.id.eq(ownerId)
					.and(review.deletedAt.isNull())
					.and(review.deletedBy.isNull())
					.and(review.isDisplayed.isTrue())
			)
			.fetch();
	}

	public List<OwnerReviewWithReplyVo> findAllActiveReviewsWithReplyByStore(UUID storeId, Long ownerId) {
		QStore store = QStore.store;
		QReview review = QReview.review;
		QReviewReply reply = QReviewReply.reviewReply;

		return queryFactory
			.select(new QOwnerReviewWithReplyVo(
				store.id,
				store.name,
				review.id,
				review.user.id,
				review.rating,
				review.content,
				review.imageUrl1,
				review.imageUrl2,
				review.imageUrl3,
				review.isDisplayed,
				reply.id,
				reply.owner.id,
				reply.content,
				reply.isDisplayed
			))
			.from(store)
			.join(store.reviews, review)
			.leftJoin(review.reviewReply, reply)
			.on(reply.deletedAt.isNull()
				.and(reply.deletedBy.isNull()))
			.where(
				store.id.eq(storeId)
					.and(store.owner.id.eq(ownerId))
					.and(review.deletedAt.isNull())
					.and(review.deletedBy.isNull())
					.and(review.isDisplayed.isTrue())
			)
			.fetch();
	}

	public Page<ManagerSearchReviewVo> searchReviewsAll(
		Long userId,
		Long ownerId,
		UUID storeId,
		UUID orderId,
		Integer rating,
		String keyword,
		Pageable pageable
	) {
		QReview review = QReview.review;
		QReviewReply reply = QReviewReply.reviewReply;

		BooleanBuilder builder = new BooleanBuilder();

		System.out.println(keyword);

		// 삭제 여부 조건 제거 → 모든 리뷰/답글 조회
		if (userId != null)
			builder.and(review.user.id.eq(userId));
		if (ownerId != null)
			builder.and(reply.owner.id.eq(ownerId));
		if (storeId != null)
			builder.and(review.store.id.eq(storeId));
		if (orderId != null)
			builder.and(review.order.id.eq(orderId));
		if (rating != null)
			builder.and(review.rating.eq(rating));
		if (keyword != null && !keyword.isBlank())
			builder.and(review.content.containsIgnoreCase(keyword));

		var query = queryFactory
			.select(new QManagerSearchReviewVo(
				review.id,
				review.user.id,
				review.order.id,
				review.store.id,
				review.rating,
				review.content,
				review.imageUrl1,
				review.imageUrl2,
				review.imageUrl3,
				review.isDisplayed,
				review.createdAt,
				review.createdBy,
				review.updatedAt,
				review.updatedBy,
				review.deletedAt,
				review.deletedBy,
				reply.id,
				reply.owner.id,
				reply.content,
				reply.isDisplayed,
				reply.createdAt,
				reply.createdBy,
				reply.updatedAt,
				reply.updatedBy,
				reply.deletedAt,
				reply.deletedBy
			))
			.from(review)
			.leftJoin(review.reviewReply, reply) // 답글이 없더라도 review는 가져오기
			.where(builder);

		long total = query.fetch().size();

		var result = query
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		return new PageImpl<>(result, pageable, total);
	}
}
