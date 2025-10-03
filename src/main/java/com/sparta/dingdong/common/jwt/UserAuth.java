package com.sparta.dingdong.common.jwt;

import com.sparta.dingdong.domain.user.entity.enums.UserRole;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserAuth {
	private final Long id;
	private final UserRole userRole;
}