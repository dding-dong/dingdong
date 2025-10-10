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
	private static final String REFRESH_TOKEN_PREFIX = "refresh:";
	private static final String BLACKLIST_PREFIX = "blacklist:";

	public boolean validateKey(String token) {
		try {
			return redisTemplate.hasKey(BLACKLIST_PREFIX + token);
		} catch (Exception e) {
			throw new RuntimeException(UserErrorCode.INVALID_REQUEST.getMessage());
		}
	}

	public void saveBlackListToken(String token, long expirationMillis) {
		try {
			redisTemplate.opsForValue().set(
				BLACKLIST_PREFIX + token,
				"logout",
				expirationMillis,
				TimeUnit.MILLISECONDS
			);
		} catch (Exception e) {
			throw new RuntimeException(UserErrorCode.INVALID_REQUEST.getMessage());
		}
	}

	// ✅ 리프레시 토큰의 jti 저장 (토큰 전체 대신 jti만 저장)
	public void saveRefreshToken(Long userId, String jti, long expirationMillis) {
		try {
			String key = REFRESH_TOKEN_PREFIX + userId;
			redisTemplate.opsForValue().set(key, jti, expirationMillis, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			throw new RuntimeException(UserErrorCode.INVALID_REQUEST.getMessage());
		}
	}

	// ✅ 토큰에서 추출한 jti와 비교
	public boolean validateRefreshToken(Long userId, String jti) {
		String key = REFRESH_TOKEN_PREFIX + userId;
		String savedJti = redisTemplate.opsForValue().get(key);
		return Objects.equals(savedJti, jti);
	}

	public void deleteRefreshToken(Long userId) {
		String key = REFRESH_TOKEN_PREFIX + userId;
		redisTemplate.delete(key);
	}

	// 메일인증번호검증 관련 코드
	//    public void saveMailAuthCode(String email, String code, Duration ttl) {
	//        redisTemplate.opsForValue().set("EMAIL_CODE:" + email, code, ttl);
	//    }
	//
	//    public String getMailAuthCode(String email) {
	//        String code = redisTemplate.opsForValue().get("EMAIL_CODE:" + email);
	//        if (code == null) {
	//            throw new BizException(MailErrorCode.EMAIL_CODE_NOT_FOUND);
	//        }
	//        return code;
	//    }
	//
	//    public void deleteMailAuthCode(String email) {
	//        redisTemplate.delete("EMAIL_CODE:" + email);
	//    }
	//
	//    public void save(String key, String value, Duration ttl) {
	//        try {
	//            redisTemplate.opsForValue().set(key, value, ttl);
	//        } catch (Exception e) {
	//            throw new BizException(MailErrorCode.SEND_FAILED);
	//        }
	//    }

	public boolean hasKey(String key) {
		return Boolean.TRUE.equals(redisTemplate.hasKey(key));
	}

	public void delete(String key) {
		redisTemplate.delete(key);
	}

}
