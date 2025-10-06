package com.sparta.dingdong.domain.review.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.dingdong.domain.review.entity.QReview;
import com.sparta.dingdong.domain.review.entity.QReviewReply;
import com.sparta.dingdong.domain.review.repository.vo.ReviewWithReplyVo;

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
			.select(Projections.constructor(
				ReviewWithReplyVo.class,
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
}
