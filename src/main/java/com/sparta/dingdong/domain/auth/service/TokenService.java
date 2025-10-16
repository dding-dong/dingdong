package com.sparta.dingdong.domain.auth.service;

import org.springframework.stereotype.Service;

import com.sparta.dingdong.common.jwt.JwtUtil;
import com.sparta.dingdong.domain.auth.dto.response.TokenResponse;
import com.sparta.dingdong.domain.user.entity.enums.UserRole;
import com.sparta.dingdong.domain.user.repository.RedisRepository;
import com.sparta.dingdong.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenService {

	private final JwtUtil jwtUtil;
	private final UserRepository userRepository;
	private final RedisRepository redisRepository;

	public TokenResponse generateTokens(Long userId, UserRole userRole, Long tokenVersion) {
		String accessToken = jwtUtil.createAccessToken(userId, userRole, tokenVersion);
		String refreshToken = jwtUtil.createRefreshToken(userId, userRole, tokenVersion);
		return new TokenResponse(accessToken, refreshToken);
	}

	public void saveAccessTokenVersion(Long userId, String accessToken) {
		long expirationMillis = jwtUtil.getExpiration(accessToken);
		long tokenVersion = jwtUtil.extractUserAuth(accessToken).getTokenVersion();
		redisRepository.saveTokenVersion(userId, tokenVersion, expirationMillis);
	}

	public void saveRefreshToken(Long userId, String refreshToken) {
		long refreshExpiration = jwtUtil.getRefreshExpiration(refreshToken);
		String jti = jwtUtil.getJti(refreshToken);
		redisRepository.saveRefreshToken(userId, jti, refreshExpiration);
	}
}