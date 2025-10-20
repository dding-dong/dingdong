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
import lombok.extern.slf4j.Slf4j;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "p_user")
@Slf4j(topic = "User Entity")
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
	@Builder.Default
	private List<Address> addressList = new ArrayList<>();

	// 양방향 연관관계: 매니저 권한 필드 접근 가능
	@OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private Manager manager;

	// 편의 메서드: 사용자 정보 업데이트
	public void updateUser(String nickname, String password, String phone) {
		if (nickname != null)
			this.nickname = nickname;
		if (password != null)
			this.password = password;
		if (phone != null)
			this.phone = phone;
	}

	public void addAddress(Address address) {
		this.addressList.add(address);
		address.setUser(this);
	}

	public void disableAllDefaultAddress() {
		if (this.addressList != null) {
			this.addressList.forEach(address -> address.updateDefault(false));
		}
	}

	// 주소 삭제 및 기본주소 처리
	public void deleteAddress(Address address) {
		if (address == null || addressList == null || !addressList.contains(address)) {
			return;
		}

		addressList.remove(address);
		address.setUser(null);

		if (address.isDefault()) {
			if (!addressList.isEmpty()) {
				addressList.get(0).updateDefault(true);
			} else {
				log.warn("마지막 기본주소가 삭제되었습니다. 현재 유저는 기본주소가 없습니다.");
			}
		}
	}
}