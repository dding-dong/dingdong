package com.sparta.dingdong.domain.auth.service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.dingdong.common.jwt.JwtUtil;
import com.sparta.dingdong.common.jwt.UserAuth;
import com.sparta.dingdong.domain.auth.dto.request.LoginRequestDto;
import com.sparta.dingdong.domain.auth.dto.response.TokenResponse;
import com.sparta.dingdong.domain.auth.exception.AuthErrorCode;
import com.sparta.dingdong.domain.store.exception.NotStoreOwnerException;
import com.sparta.dingdong.domain.user.entity.User;
import com.sparta.dingdong.domain.user.entity.enums.UserRole;
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

	/**
	 * ✅ 로그인: Redis 기반 버전 관리
	 */
	public TokenResponse login(LoginRequestDto requestDto) {
		User user = userRepository.findByEmailOrElseThrow(requestDto.getEmail());

		if (user.isDeleted()) {
			throw new RuntimeException(UserErrorCode.USER_DELETED.getMessage());
		}

		if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
			throw new RuntimeException(UserErrorCode.INVALID_PASSWORD.getMessage());
		}

		// 1️⃣ Redis에서 기존 버전 확인, 없으면 1로 초기화
		Long redisTokenVersion = redisRepository.getTokenVersion(user.getId());
		Long tokenVersion = (redisTokenVersion != null) ? redisTokenVersion : 1L;

		// 2️⃣ 토큰 발급 (JWT에 version 포함)
		TokenResponse tokenResponse = tokenService.generateTokens(user.getId(), user.getUserRole(), tokenVersion);

		// 3️⃣ Redis에 버전 + refresh jti 저장
		tokenService.saveAccessTokenVersion(user.getId(), tokenResponse.getAccessToken());
		tokenService.saveRefreshToken(user.getId(), tokenResponse.getRefreshToken());

		return tokenResponse;
	}

	/**
	 * ✅ 로그아웃: Redis 버전 증가 → 토큰 무효화
	 */
	@Transactional
	public void logout(HttpServletRequest request) {
		String token = jwtUtil.extractToken(request);

		if (token == null) {
			log.warn("로그아웃 실패: Authorization 헤더에 토큰이 없습니다.");
			return;
		}

		try {
			UserAuth userAuth = jwtUtil.extractUserAuth(token);

			// 2️⃣ Redis에 새 버전 저장 (TTL은 AccessToken 만료 시간 기준)
			long accessExpiration = jwtUtil.getExpiration(token);
			redisRepository.incrementTokenVersion(userAuth.getId(), accessExpiration);

			// 3️⃣ RefreshToken 제거
			redisRepository.deleteRefreshToken(userAuth.getId());
			
		} catch (Exception e) {
			log.warn("로그아웃 중 예외 발생: {}", e.getMessage());
		}
	}

	/**
	 * ✅ 리프레시 토큰 재발급
	 */
	public TokenResponse reissue(String bearerToken) {
		if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
			throw new RuntimeException(AuthErrorCode.MISMATCHED_REFRESH_TOKEN.getMessage());
		}
		String refreshToken = bearerToken.substring(7);

		if (!jwtUtil.validateToken(refreshToken)) {
			throw new RuntimeException(AuthErrorCode.MISMATCHED_REFRESH_TOKEN.getMessage());
		}

		UserAuth userAuth = jwtUtil.extractUserAuth(refreshToken);
		Long userId = userAuth.getId();

		// ✅ 1️⃣ Redis에 저장된 jti 검증
		if (!redisRepository.validateRefreshToken(userId, jwtUtil.getJti(refreshToken))) {
			throw new RuntimeException(AuthErrorCode.REUSED_REFRESH_TOKEN.getMessage());
		}

		// ✅ 2️⃣ Redis의 tokenVersion과 비교
		Long redisVersion = redisRepository.getTokenVersion(userId);
		if (redisVersion == null || !redisVersion.equals(userAuth.getTokenVersion())) {
			throw new RuntimeException(AuthErrorCode.MISMATCHED_REFRESH_TOKEN.getMessage());
		}

		// ✅ 3️⃣ 기존 RefreshToken 삭제 후 새 토큰 발급
		redisRepository.deleteRefreshToken(userId);

		TokenResponse newTokens = tokenService.generateTokens(userId, userAuth.getUserRole(), redisVersion);
		tokenService.saveRefreshToken(userId, newTokens.getRefreshToken());

		log.info("리프레시 토큰 재발급 완료: userId={}, version={}", userId, redisVersion);
		return newTokens;
	}

	/**
	 * OWNER는 본인 가게만 접근 가능 (MANAGER, MASTER는 전체 접근 가능)
	 */
	public void validateStoreOwnership(UserAuth user, Long storeOwnerId) {
		UserRole role = user.getUserRole();

		if (role == UserRole.OWNER) {
			if (!storeOwnerId.equals(user.getId())) {
				throw new NotStoreOwnerException();
			}
		}
	}

	/**
	 * 관리자 여부 확인
	 */
	public boolean isAdmin(UserAuth user) {
		return user != null &&
			(user.getUserRole() == UserRole.MASTER || user.getUserRole() == UserRole.MANAGER);
	}

	/**
	 * 관리자만 접근 가능하도록 강제
	 */
	public void ensureAdmin(UserAuth user) {
		if (!isAdmin(user)) {
			throw new AccessDeniedException("관리자 권한이 필요합니다.");
		}
	}
}
