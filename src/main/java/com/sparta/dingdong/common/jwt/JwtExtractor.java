package com.sparta.dingdong.common.jwt;

import static com.sparta.dingdong.common.jwt.JwtConstants.*;

import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtExtractor {

	/**
	 * HTTP 요청 헤더에서 Bearer 토큰 추출
	 */
	public String extractToken(HttpServletRequest request) {
		String bearer = request.getHeader(HEADER_AUTHORIZATION);
		if (bearer != null && bearer.startsWith(TOKEN_PREFIX)) {
			return bearer.substring(TOKEN_PREFIX.length());
		}
		return null;
	}

	/**
	 * RefreshToken 요청 헤더에서 Bearer 토큰 추출
	 */
	public String extractToken(String bearerHeader) {
		if (bearerHeader != null && bearerHeader.startsWith(TOKEN_PREFIX)) {
			return bearerHeader.substring(TOKEN_PREFIX.length());
		}
		return null;
	}
}
