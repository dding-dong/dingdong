package com.sparta.dingdong.domain.user.entity.enums;

import java.util.Arrays;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {

	CUSTOMER("CUSTOMER"),
	OWNER("OWNER"),
	MANAGER("MANAGER"),
	MASTER("MASTER");

	private final String value;

	public static UserRole of(String role) {
		return Arrays.stream(UserRole.values())
			.filter(r -> r.name().equalsIgnoreCase(role))
			.findFirst()
			.orElseThrow(() -> new RuntimeException("유효하지 않은 UerRole"));
	}
}