package com.sparta.dingdong.common.security;

import java.util.Arrays;

public class SecurityUrlMatcher {

	// 공용 URL: 인증 없이 접근 가능
	public static final String[] PUBLIC_URLS = {
		"/v1/users/signup",
		"/v1/auth/login",
		"/v1/auth/reissue"  // 토큰 재발급 URL 포함
	};

	// CUSTOMER 전용 URL
	public static final String[] CUSTOMER_URLS = {
		"/api/customer/**"
	};

	// OWNER 전용 URL
	public static final String[] OWNER_URLS = {
		"/api/owner/**"
	};

	// MANAGER 전용 URL
	public static final String[] MANAGER_URLS = {
		"/api/manager/**"
	};

	// MASTER 전용 URL
	public static final String[] MASTER_URLS = {
		"/api/master/**",
		"/api/admin/**"  // 운영자/관리자 URL 포함
	};

	// 유틸 메서드
	public static boolean isPublicUrl(String path) {
		return Arrays.stream(PUBLIC_URLS).anyMatch(path::startsWith);
	}

	public static boolean isCustomerUrl(String path) {
		return Arrays.stream(CUSTOMER_URLS).anyMatch(path::startsWith);
	}

	public static boolean isOwnerUrl(String path) {
		return Arrays.stream(OWNER_URLS).anyMatch(path::startsWith);
	}

	public static boolean isManagerUrl(String path) {
		return Arrays.stream(MANAGER_URLS).anyMatch(path::startsWith);
	}

	public static boolean isMasterUrl(String path) {
		return Arrays.stream(MASTER_URLS).anyMatch(path::startsWith);
	}
}
