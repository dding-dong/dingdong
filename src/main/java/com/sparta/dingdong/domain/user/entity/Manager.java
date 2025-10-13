package com.sparta.dingdong.domain.user.entity;

import com.sparta.dingdong.common.base.BaseEntity;
import com.sparta.dingdong.domain.user.entity.enums.ManagerStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
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
@Table(name = "p_manager")
public class Manager extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@MapsId // JPA에서 기존 엔티티의 기본 키를 그대로 사용하면서 다른 엔티티와 1:1 매핑할 때 쓰이는 어노테이션
	@JoinColumn(name = "id")
	private User user;

	@Enumerated(EnumType.STRING)
	private ManagerStatus managerStatus;

	public void approve() {
		if (managerStatus != ManagerStatus.PENDING) {
			throw new IllegalStateException("대기 상태의 매니저만 승인할 수 있습니다.");
		}
		this.managerStatus = ManagerStatus.APPROVED;
	}

	public void activate() {
		if (managerStatus != ManagerStatus.APPROVED) {
			throw new IllegalStateException("승인된 매니저만 활성화할 수 있습니다.");
		}
		this.managerStatus = ManagerStatus.ACTIVE;
	}

	public void suspend() {
		if (managerStatus != ManagerStatus.ACTIVE) {
			throw new IllegalStateException("활성화된 매니저만 정지할 수 있습니다.");
		}
		this.managerStatus = ManagerStatus.SUSPENDED;
	}

	public void reject() {
		if (managerStatus != ManagerStatus.PENDING) {
			throw new IllegalStateException("대기 상태의 매니저만 거절할 수 있습니다.");
		}
		this.managerStatus = ManagerStatus.DELETED;
	}

	public void softDeleteByMaster(Long masterId) {
		if (isDeleted()) {
			throw new IllegalStateException("이미 삭제된 매니저입니다.");
		}
		super.softDelete(masterId);
		this.managerStatus = ManagerStatus.DELETED;
	}
}
