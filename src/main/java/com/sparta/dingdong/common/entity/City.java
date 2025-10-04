package com.sparta.dingdong.common.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "p_city") // 시/도를 저장하는 테이블
public class City {

	@Id
	@Column(length = 2) // "02" (서울), "31" (경기)
	private String id;

	@Column(nullable = false, length = 50)
	private String name;

	@OneToMany(mappedBy = "city", fetch = FetchType.LAZY,
		cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Gu> guList = new ArrayList<>();

	// 정적 팩토리 메서드
	public static City of(String id, String name) {
		return new City(id, name);
	}

	// 생성자
	private City(String id, String name) {
		this.id = id;
		this.name = name;
		this.guList = new ArrayList<>();
	}

	// 편의 메서드
	public void addGu(Gu gu) {
		guList.add(gu);
		gu.setCity(this);
	}
}
