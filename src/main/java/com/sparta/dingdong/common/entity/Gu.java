package com.sparta.dingdong.common.entity;

import java.util.List;

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
@Table(name = "p_gu")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Gu {

	@Id
	@GeneratedValue
	private Long id;   // 구 ID

	@Column(nullable = false)
	private String name;  // 구 이름

	@Column(nullable = false)
	private Boolean status = true;  // 사용 여부 (true = 활성, false = 비활성)

	// 연관 관계 (Gu : City = N : 1)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "city_id", nullable = false)
	private City city;

	// 연관 관계 (Gu : Dong = 1 : N)
	@OneToMany(mappedBy = "gu", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Dong> dongList;

}
