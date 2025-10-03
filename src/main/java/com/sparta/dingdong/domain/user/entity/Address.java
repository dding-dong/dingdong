package com.sparta.dingdong.domain.user.entity;

import com.sparta.dingdong.common.entity.Dong;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "p_address")
public class Address {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 유저 (N:1)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	// 동(행정동 등)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "dong_id", nullable = false)
	private Dong dong;

	// 상세주소
	@Column(nullable = false, length = 255)
	private String detailAddress;

	@Column(name = "postal_code", nullable = false, length = 20)
	private String postalCode;

	// 기본 주소 여부
	@Column(name = "is_default", nullable = false)
	private boolean isDefault = false;

	// ==== 연관관계 편의 메서드 ====
	public void setUser(User user) {
		this.user = user;
		if (!user.getAddressList().contains(this)) {
			user.getAddressList().add(this);
		}
	}

	public void updateAddress(String detailAddress, String postalCode, Dong dong, boolean isDefault) {
		if (detailAddress != null)
			this.detailAddress = detailAddress;
		if (postalCode != null)
			this.postalCode = postalCode;
		if (dong != null)
			this.dong = dong;
		this.isDefault = isDefault;
	}
}