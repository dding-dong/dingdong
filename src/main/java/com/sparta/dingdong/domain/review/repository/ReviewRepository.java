package com.sparta.dingdong.domain.review.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sparta.dingdong.domain.order.entity.Order;
import com.sparta.dingdong.domain.review.entity.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {

	Optional<Review> findByOrder(Order order);
}
