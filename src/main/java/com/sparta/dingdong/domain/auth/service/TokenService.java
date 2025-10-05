package com.sparta.dingdong.domain.auth.service;

import org.springframework.stereotype.Service;

import com.sparta.dingdong.common.jwt.JwtUtil;
import com.sparta.dingdong.domain.auth.dto.response.TokenResponse;
import com.sparta.dingdong.domain.user.entity.enums.UserRole;
import com.sparta.dingdong.domain.user.repository.RedisRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenService {

	private final JwtUtil jwtUtil;
	private final RedisRepository redisRepository;

	public TokenResponse generateTokens(Long userId, UserRole userRole) {
		String accessToken = jwtUtil.createToken(userId, userRole);
		String refreshToken = jwtUtil.createRefreshToken(userId, userRole);
		return new TokenResponse(accessToken, refreshToken);
	}

	public void saveRefreshToken(Long userId, String refreshToken) {
		long refreshExpiration = jwtUtil.getRefreshExpiration(refreshToken);
		redisRepository.saveRefreshToken(userId, refreshToken, refreshExpiration);
	}
}