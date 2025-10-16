package com.sparta.dingdong.domain.user.repository;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.sparta.dingdong.domain.user.exception.UserErrorCode;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RedisRepository {

	private final RedisTemplate<String, String> redisTemplate;
	private static final String REFRESH_TOKEN_PREFIX = "auth:refresh:%d";
	private static final String TOKEN_VERSION_PREFIX = "auth:version:%d";

	// ✅ 리프레시 토큰의 jti 저장 (토큰 전체 대신 jti만 저장)
	public void saveRefreshToken(Long userId, String jti, Long refreshExpiration) {
		try {
			String key = String.format(REFRESH_TOKEN_PREFIX, userId);
			redisTemplate.opsForValue().set(key, jti, refreshExpiration, java.util.concurrent.TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			throw new RuntimeException(UserErrorCode.INVALID_REQUEST.getMessage());
		}
	}

	// ✅ 토큰에서 추출한 jti와 비교
	public boolean validateRefreshToken(Long userId, String jti) {
		String key = String.format(REFRESH_TOKEN_PREFIX, userId);
		String savedJti = redisTemplate.opsForValue().get(key);
		return Objects.equals(savedJti, jti);
	}

	public void deleteRefreshToken(Long userId) {
		String key = REFRESH_TOKEN_PREFIX + userId;
		redisTemplate.delete(key);
	}

	public void saveTokenVersion(Long userId, Long tokenVersion, long accessExpiration) {
		String key = String.format(TOKEN_VERSION_PREFIX, userId);
		redisTemplate.opsForValue().set(key, String.valueOf(tokenVersion), accessExpiration, TimeUnit.MILLISECONDS);
	}

	public Long getTokenVersion(Long userId) {
		String key = String.format(TOKEN_VERSION_PREFIX, userId);
		String value = redisTemplate.opsForValue().get(key);
		return value != null ? Long.parseLong(value) : null;
	}

	public boolean hasKey(String key) {
		return Boolean.TRUE.equals(redisTemplate.hasKey(key));
	}

	public void delete(String key) {
		redisTemplate.delete(key);
	}
}
