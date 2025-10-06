package com.sparta.dingdong.domain.store.entity;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.sparta.dingdong.common.base.BaseEntity;
import com.sparta.dingdong.domain.category.entity.StoreCategory;
import com.sparta.dingdong.domain.review.entity.Review;
import com.sparta.dingdong.domain.review.entity.ReviewReply;
import com.sparta.dingdong.domain.store.entity.enums.DayOfWeek;
import com.sparta.dingdong.domain.user.entity.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "p_store")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Store extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	// 점주 --> User 참조할 경우 --> User 객체 바로 접근 가능
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "owner_user_id", nullable = false)
	private User owner;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id", nullable = false)
	private StoreCategory category;

	@Column(nullable = false)
	private String name;

	private String imageUrl;

	@Column(nullable = false)
	private String address;

	@Column(name = "postal_code", nullable = false)
	private String postalCode;

	@Column(name = "min_order_price")
	private BigInteger minOrderPrice;

	private String status;

	private LocalDateTime openedAt;
	private LocalDateTime closedAt;

	// CSV로 요일 저장
	@Column(name = "day_of_week")
	private String daysOfWeekCsv;

	// helper 메소드
	@Transient
	public Set<DayOfWeek> getDaysOfWeek() {
		return DayOfWeek.fromCsv(daysOfWeekCsv);
	}

	@OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Review> reviews = new ArrayList<>();

	@OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ReviewReply> reviewReplies = new ArrayList<>();

	public void setDaysOfWeek(Set<DayOfWeek> days) {
		this.daysOfWeekCsv = DayOfWeek.toCsv(days);
	}
}
