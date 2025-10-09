package com.sparta.dingdong.domain.payment.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sparta.dingdong.domain.order.entity.Order;
import com.sparta.dingdong.domain.payment.entity.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

	Optional<Payment> findByOrder(Order order);
}
