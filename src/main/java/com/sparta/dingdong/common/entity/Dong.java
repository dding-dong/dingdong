package com.sparta.dingdong.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "p_dong")
public class Dong {

	@Id
	@Column(length = 3) // 동 코드
	private String id;

	@Column(nullable = false, length = 50)
	private String name;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "gu_id", nullable = false)
	private Gu gu;

	// 정적 팩토리 메서드
	public static Dong of(String id, String name, Gu gu) {
		Dong dong = new Dong();
		dong.id = id;
		dong.name = name;
		dong.gu = gu;
		return dong;
	}

	// Gu 세팅용
	public void setGu(Gu gu) {
		this.gu = gu;
	}
}
