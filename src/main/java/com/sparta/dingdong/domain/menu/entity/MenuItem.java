package com.sparta.dingdong.domain.menu.entity;

import java.math.BigInteger;
import java.util.UUID;

import com.sparta.dingdong.common.base.BaseEntity;
import com.sparta.dingdong.domain.store.entity.Store;

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
import lombok.Setter;

@Entity
@Table(name = "p_menu_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItem extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "store_id", nullable = false)
	private Store store;

	@Column(nullable = false)
	private String name;

	private String images;

	@Column(nullable = false)
	private BigInteger price;

	@Column(nullable = false)
	@Builder.Default
	private Boolean isActive = true;

	@Column(nullable = false)
	@Builder.Default
	private Boolean isRecommended = false;

	private String content;

	@Column(nullable = false)
	@Builder.Default
	private Boolean isAiUsed = false;

	private Boolean isDisplayed;
	private Boolean isSoldout;
	private String aiContent;
	private String imageUrl;
}
