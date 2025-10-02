package com.sparta.dingdong.domain.review.entity;

import java.util.UUID;

import com.sparta.dingdong.common.base.BaseEntity;
import com.sparta.dingdong.domain.order.entity.Order;
import com.sparta.dingdong.domain.store.entity.Store;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "p_review")
public class Review extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@OneToOne
	@JoinColumn(name = "order_id")
	private Order order;

	@ManyToOne
	@JoinColumn(name = "store_id")
	private Store store;

	private Integer rating;

	private String content;

	private String imageUrl1;

	private String imageUrl2;

	private String imageUrl3;

	private boolean isDisplayed;
}
