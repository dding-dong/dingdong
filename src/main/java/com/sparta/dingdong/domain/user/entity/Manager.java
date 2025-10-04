package com.sparta.dingdong.domain.user.entity;

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
import lombok.Setter;

@Setter
@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "p_manager")
public class Manager {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@MapsId // JPA에서 기존 엔티티의 기본 키를 그대로 사용하면서 다른 엔티티와 1:1 매핑할 때 쓰이는 어노테이션
	@JoinColumn(name = "id")
	private User user;

	@Enumerated(EnumType.STRING)
	private ManagerStatus managerStatus;
}
