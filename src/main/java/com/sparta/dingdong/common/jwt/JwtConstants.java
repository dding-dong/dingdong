package com.sparta.dingdong.common.jwt;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JwtConstants {
	public static final String HEADER_AUTHORIZATION = "Authorization";
	public static final String TOKEN_PREFIX = "Bearer ";
	public static final String CLAIM_USER_ROLE = "userRole";
	public static final String CLAIM_JTI = "jti";
}

