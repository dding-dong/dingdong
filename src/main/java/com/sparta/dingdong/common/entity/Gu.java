package com.sparta.dingdong.common.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "p_gu")
public class Gu {

	@Id
	@Column(length = 2) // 구 코드
	private String id;

	@Column(nullable = false, length = 50)
	private String name;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "city_id", nullable = false)
	private City city;

	@OneToMany(mappedBy = "gu", fetch = FetchType.LAZY,
		cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Dong> dongList = new ArrayList<>();

	// 정적 팩토리 메서드
	public static Gu of(String id, String name, City city) {
		Gu gu = new Gu();
		gu.id = id;
		gu.name = name;
		gu.city = city;
		gu.dongList = new ArrayList<>();
		return gu;
	}

	// 편의 메서드
	public void addDong(Dong dong) {
		dongList.add(dong);
		dong.setGu(this);
	}

	// City 세팅용
	public void setCity(City city) {
		this.city = city;
	}
}
