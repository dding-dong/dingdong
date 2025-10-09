package com.sparta.dingdong.domain.auth.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sparta.dingdong.common.jwt.JwtUtil;
import com.sparta.dingdong.domain.auth.dto.response.TokenResponse;
import com.sparta.dingdong.domain.user.entity.enums.UserRole;
import com.sparta.dingdong.domain.user.repository.RedisRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("TokenService 단위 테스트")
class TokenServiceTest {

	@InjectMocks
	private TokenService tokenService;

	@Mock
	private JwtUtil jwtUtil;

	@Mock
	private RedisRepository redisRepository;

	private Long userId;
	private UserRole userRole;
	private String accessToken;
	private String refreshToken;

	@BeforeEach
	void setUp() {
		userId = 1L;
		userRole = UserRole.CUSTOMER;
		accessToken = "accessToken123";
		refreshToken = "refreshToken123";
	}

	@Nested
	@DisplayName("generateTokens 메서드")
	class GenerateTokensTests {

		@Test
		@DisplayName("성공: 액세스 토큰과 리프레시 토큰 생성")
		void generateTokens_Success() {
			// given
			given(jwtUtil.createToken(userId, userRole)).willReturn(accessToken);
			given(jwtUtil.createRefreshToken(userId, userRole)).willReturn(refreshToken);

			// when
			TokenResponse result = tokenService.generateTokens(userId, userRole);

			// then
			assertThat(result).isNotNull();
			assertThat(result.getAccessToken()).isEqualTo(accessToken);
			assertThat(result.getRefreshToken()).isEqualTo(refreshToken);
			verify(jwtUtil).createToken(userId, userRole);
			verify(jwtUtil).createRefreshToken(userId, userRole);
		}

		@Test
		@DisplayName("성공: CUSTOMER 권한으로 토큰 생성")
		void generateTokens_CustomerRole() {
			// given
			UserRole role = UserRole.CUSTOMER;
			given(jwtUtil.createToken(userId, role)).willReturn(accessToken);
			given(jwtUtil.createRefreshToken(userId, role)).willReturn(refreshToken);

			// when
			TokenResponse result = tokenService.generateTokens(userId, role);

			// then
			assertThat(result).isNotNull();
			verify(jwtUtil).createToken(userId, role);
			verify(jwtUtil).createRefreshToken(userId, role);
		}

		@Test
		@DisplayName("성공: OWNER 권한으로 토큰 생성")
		void generateTokens_OwnerRole() {
			// given
			UserRole role = UserRole.OWNER;
			given(jwtUtil.createToken(userId, role)).willReturn(accessToken);
			given(jwtUtil.createRefreshToken(userId, role)).willReturn(refreshToken);

			// when
			TokenResponse result = tokenService.generateTokens(userId, role);

			// then
			assertThat(result).isNotNull();
			verify(jwtUtil).createToken(userId, role);
			verify(jwtUtil).createRefreshToken(userId, role);
		}

		@Test
		@DisplayName("성공: MANAGER 권한으로 토큰 생성")
		void generateTokens_ManagerRole() {
			// given
			UserRole role = UserRole.MANAGER;
			given(jwtUtil.createToken(userId, role)).willReturn(accessToken);
			given(jwtUtil.createRefreshToken(userId, role)).willReturn(refreshToken);

			// when
			TokenResponse result = tokenService.generateTokens(userId, role);

			// then
			assertThat(result).isNotNull();
			verify(jwtUtil).createToken(userId, role);
			verify(jwtUtil).createRefreshToken(userId, role);
		}

		@Test
		@DisplayName("성공: MASTER 권한으로 토큰 생성")
		void generateTokens_MasterRole() {
			// given
			UserRole role = UserRole.MASTER;
			given(jwtUtil.createToken(userId, role)).willReturn(accessToken);
			given(jwtUtil.createRefreshToken(userId, role)).willReturn(refreshToken);

			// when
			TokenResponse result = tokenService.generateTokens(userId, role);

			// then
			assertThat(result).isNotNull();
			verify(jwtUtil).createToken(userId, role);
			verify(jwtUtil).createRefreshToken(userId, role);
		}

		@Test
		@DisplayName("실패: 액세스 토큰 생성 실패")
		void generateTokens_AccessTokenCreationFailed() {
			// given
			given(jwtUtil.createToken(userId, userRole))
				.willThrow(new RuntimeException("토큰 생성 실패"));

			// when & then
			assertThatThrownBy(() -> tokenService.generateTokens(userId, userRole))
				.isInstanceOf(RuntimeException.class)
				.hasMessageContaining("토큰 생성 실패");
		}

		@Test
		@DisplayName("실패: 리프레시 토큰 생성 실패")
		void generateTokens_RefreshTokenCreationFailed() {
			// given
			given(jwtUtil.createToken(userId, userRole)).willReturn(accessToken);
			given(jwtUtil.createRefreshToken(userId, userRole))
				.willThrow(new RuntimeException("리프레시 토큰 생성 실패"));

			// when & then
			assertThatThrownBy(() -> tokenService.generateTokens(userId, userRole))
				.isInstanceOf(RuntimeException.class)
				.hasMessageContaining("리프레시 토큰 생성 실패");
		}
	}

	@Nested
	@DisplayName("saveRefreshToken 메서드")
	class SaveRefreshTokenTests {

		@Test
		@DisplayName("성공: 리프레시 토큰 Redis에 저장")
		void saveRefreshToken_Success() {
			// given
			long expiration = 3600000L;
			given(jwtUtil.getRefreshExpiration(refreshToken)).willReturn(expiration);

			// when
			tokenService.saveRefreshToken(userId, refreshToken);

			// then
			verify(jwtUtil).getRefreshExpiration(refreshToken);
			verify(redisRepository).saveRefreshToken(userId, refreshToken, expiration);
		}

		@Test
		@DisplayName("성공: 다양한 만료 시간으로 저장")
		void saveRefreshToken_DifferentExpirations() {
			// given
			long[] expirations = {1000L, 60000L, 3600000L, 86400000L};

			for (long expiration : expirations) {
				// given
				given(jwtUtil.getRefreshExpiration(refreshToken)).willReturn(expiration);

				// when
				tokenService.saveRefreshToken(userId, refreshToken);

				// then
				verify(redisRepository).saveRefreshToken(userId, refreshToken, expiration);
			}
		}

		@Test
		@DisplayName("실패: 만료 시간 조회 실패")
		void saveRefreshToken_ExpirationRetrievalFailed() {
			// given
			given(jwtUtil.getRefreshExpiration(refreshToken))
				.willThrow(new RuntimeException("만료 시간 조회 실패"));

			// when & then
			assertThatThrownBy(() -> tokenService.saveRefreshToken(userId, refreshToken))
				.isInstanceOf(RuntimeException.class)
				.hasMessageContaining("만료 시간 조회 실패");
		}

		@Test
		@DisplayName("실패: Redis 저장 실패")
		void saveRefreshToken_RedisSaveFailed() {
			// given
			long expiration = 3600000L;
			given(jwtUtil.getRefreshExpiration(refreshToken)).willReturn(expiration);
			willThrow(new RuntimeException("Redis 저장 실패"))
				.given(redisRepository).saveRefreshToken(userId, refreshToken, expiration);

			// when & then
			assertThatThrownBy(() -> tokenService.saveRefreshToken(userId, refreshToken))
				.isInstanceOf(RuntimeException.class)
				.hasMessageContaining("Redis 저장 실패");
		}
	}
}