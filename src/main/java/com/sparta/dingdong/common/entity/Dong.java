package com.sparta.dingdong.common.entity;

import java.util.ArrayList;
import java.util.List;

import com.sparta.dingdong.domain.user.entity.Address;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "p_dong")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dong {

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "gu_id", nullable = false)
	private Gu gu; // 구 참조

	@Column(nullable = false, length = 100)
	private String name;

	@Column(nullable = false)
	private Boolean status = true;

	@OneToMany(mappedBy = "dong", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Address> addresses = new ArrayList<>();
}
