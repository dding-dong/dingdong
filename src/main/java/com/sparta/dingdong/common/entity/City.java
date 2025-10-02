package com.sparta.dingdong.common.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "p_city")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class City {

	@Id
	@GeneratedValue
	private Long id;   // 도시 ID

	@Column(nullable = false)
	private String name;  // 도시 이름

	@Column(nullable = false)
	private Boolean status = true;  // 사용 여부 (true = 활성, false = 비활성)

	// 연관 관계 (City : Gu = 1 : N)
	@OneToMany(mappedBy = "city", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Gu> guList;
}
