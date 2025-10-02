package com.sparta.dingdong.domain.user.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ManagerStatus {
	PENDING("PENDING"),
	APPROVED("APPROVED"),
	ACTIVE("ACTIVE"),
	SUSPENDED("SUSPENDED"),
	DELETED("DELETE");

	private final String value;
}
