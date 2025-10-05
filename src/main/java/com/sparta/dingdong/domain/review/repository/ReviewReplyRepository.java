package com.sparta.dingdong.domain.review.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sparta.dingdong.domain.review.entity.Review;
import com.sparta.dingdong.domain.review.entity.ReviewReply;

@Repository
public interface ReviewReplyRepository extends JpaRepository<ReviewReply, UUID> {

	Optional<ReviewReply> findByReview(Review review);
}
