package com.sparta.dingdong.domain.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.dingdong.common.jwt.JwtUtil;
import com.sparta.dingdong.common.jwt.UserAuth;
import com.sparta.dingdong.domain.auth.dto.request.LoginRequestDto;
import com.sparta.dingdong.domain.auth.dto.response.TokenResponse;
import com.sparta.dingdong.domain.auth.exception.AuthErrorCode;
import com.sparta.dingdong.domain.user.entity.User;
import com.sparta.dingdong.domain.user.exception.UserErrorCode;
import com.sparta.dingdong.domain.user.repository.RedisRepository;
import com.sparta.dingdong.domain.user.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional // AuthService의 모든 public 메서드에만 트랜잭션 적용
@RequiredArgsConstructor
@Slf4j(topic = "AuthService")
public class AuthService {

	private final UserRepository userRepository;
	private final RedisRepository redisRepository;
	private final PasswordEncoder passwordEncoder;
	private final TokenService tokenService;

	private final JwtUtil jwtUtil;

	public TokenResponse login(LoginRequestDto requestDto) {
		User user = userRepository.findByEmailOrElseThrow(requestDto.getEmail());

		if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
			throw new RuntimeException(UserErrorCode.INVALID_PASSWORD.getMessage());
		}

		TokenResponse tokenResponse = tokenService.generateTokens(user.getId(), user.getUserRole());
		tokenService.saveRefreshToken(user.getId(), tokenResponse.getRefreshToken());

		return tokenResponse;
	}

	public void logout(HttpServletRequest request) {
		try {
			String token = jwtUtil.extractToken(request);

			if (token != null && jwtUtil.validateToken(token)) {
				long expiration = jwtUtil.getExpiration(token);
				redisRepository.saveBlackListToken(token, expiration);

				UserAuth userAuth = jwtUtil.extractUserAuth(token);
				redisRepository.deleteRefreshToken(userAuth.getId());
			}
		} catch (Exception e) {
			// 로그만 남기고 조용히 무시
			log.warn("로그아웃 처리 중 예외 발생: {}", e.getMessage());
		}
	}

	public TokenResponse reissue(String bearerToken) {
		// 1. Bearer 제거
		if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
			throw new RuntimeException(AuthErrorCode.MISMATCHED_REFRESH_TOKEN.getMessage());
		}
		String refreshToken = bearerToken.substring(7);

		// 2. 토큰 유효성 검증
		if (!jwtUtil.validateToken(refreshToken)) {
			throw new RuntimeException(AuthErrorCode.MISMATCHED_REFRESH_TOKEN.getMessage());
		}

		// 3. 유저 정보 추출
		UserAuth userAuth = jwtUtil.extractUserAuth(refreshToken);

		// 4. Redis에 저장된 Refresh Token과 일치하는지 확인
		if (!redisRepository.validateRefreshToken(userAuth.getId(), refreshToken)) {
			throw new RuntimeException(AuthErrorCode.REUSED_REFRESH_TOKEN.getMessage());
		}

		redisRepository.deleteRefreshToken(userAuth.getId());

		// 4. 새 토큰 생성 및 저장
		TokenResponse newTokens = tokenService.generateTokens(userAuth.getId(), userAuth.getUserRole());
		tokenService.saveRefreshToken(userAuth.getId(), newTokens.getRefreshToken());

		return newTokens;
	}
}