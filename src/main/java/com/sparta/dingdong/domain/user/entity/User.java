package com.sparta.dingdong.domain.user.entity;

import java.util.ArrayList;
import java.util.List;

import com.sparta.dingdong.common.base.BaseEntity;
import com.sparta.dingdong.domain.user.entity.enums.UserRole;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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
@Table(name = "p_user")
public class User extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 50)
	private String username;

	@Column(nullable = false, length = 50)
	private String nickname;

	@Column(nullable = false, length = 100)
	private String email;

	@Column(nullable = false)
	private String password;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private UserRole userRole;

	@Column(nullable = false, length = 20)
	private String phone;

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Address> addressList = new ArrayList<>();

	// 양방향 연관관계: 매니저 권한 필드 접근 가능
	@OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private Manager manager;

	// 편의 메서드: 사용자 정보 업데이트
	public void updateUser(String nickname, String password) {
		if (nickname != null)
			this.nickname = nickname;
		if (password != null)
			this.password = password;
	}

}