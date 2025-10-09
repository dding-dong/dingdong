package com.sparta.dingdong.common.jwt;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.sparta.dingdong.domain.user.entity.enums.UserRole;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@DisplayName("JwtTokenProvider 단위 테스트")
class JwtTokenProviderTest {

	private JwtTokenProvider jwtTokenProvider;
	private String secretKey;
	private long accessTokenExpiration;
	private long refreshTokenExpiration;

	@BeforeEach
	void setUp() {
		jwtTokenProvider = new JwtTokenProvider();
		secretKey = "test-secret-key-that-is-long-enough-for-hmac-sha-256-algorithm-requirement";
		accessTokenExpiration = 1800000L; // 30 minutes
		refreshTokenExpiration = 1209600000L; // 14 days

		ReflectionTestUtils.setField(jwtTokenProvider, "secretKey", secretKey);
		ReflectionTestUtils.setField(jwtTokenProvider, "accessTokenExpiration", accessTokenExpiration);
		ReflectionTestUtils.setField(jwtTokenProvider, "refreshTokenExpiration", refreshTokenExpiration);
	}

	@Nested
	@DisplayName("createAccessToken 메서드")
	class CreateAccessTokenTests {

		@Test
		@DisplayName("성공: 유효한 액세스 토큰 생성")
		void createAccessToken_Success() {
			// given
			Long userId = 1L;
			UserRole role = UserRole.CUSTOMER;

			// when
			String token = jwtTokenProvider.createAccessToken(userId, role);

			// then
			assertThat(token).isNotNull();
			assertThat(token).isNotEmpty();

			// Verify token contents
			Claims claims = Jwts.parserBuilder()
				.setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
				.build()
				.parseClaimsJws(token)
				.getBody();

			assertThat(claims.getSubject()).isEqualTo("1");
			assertThat(claims.get("userRole", String.class)).isEqualTo("CUSTOMER");
			assertThat(claims.get("jti")).isNull(); // Access token should not have JTI
		}

		@Test
		@DisplayName("성공: 다양한 UserRole로 토큰 생성")
		void createAccessToken_DifferentRoles() {
			// given
			Long userId = 1L;

			// when & then
			for (UserRole role : UserRole.values()) {
				String token = jwtTokenProvider.createAccessToken(userId, role);
				assertThat(token).isNotNull();

				Claims claims = Jwts.parserBuilder()
					.setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
					.build()
					.parseClaimsJws(token)
					.getBody();

				assertThat(claims.get("userRole", String.class)).isEqualTo(role.name());
			}
		}
	}

	@Nested
	@DisplayName("createRefreshToken 메서드")
	class CreateRefreshTokenTests {

		@Test
		@DisplayName("성공: 유효한 리프레시 토큰 생성 (JTI 포함)")
		void createRefreshToken_Success() {
			// given
			Long userId = 1L;
			UserRole role = UserRole.CUSTOMER;

			// when
			String token = jwtTokenProvider.createRefreshToken(userId, role);

			// then
			assertThat(token).isNotNull();
			assertThat(token).isNotEmpty();

			// Verify token contents
			Claims claims = Jwts.parserBuilder()
				.setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
				.build()
				.parseClaimsJws(token)
				.getBody();

			assertThat(claims.getSubject()).isEqualTo("1");
			assertThat(claims.get("userRole", String.class)).isEqualTo("CUSTOMER");
			assertThat(claims.get("jti", String.class)).isNotNull(); // Refresh token should have JTI
		}

		@Test
		@DisplayName("성공: 매번 다른 JTI 생성")
		void createRefreshToken_UniqueJti() {
			// given
			Long userId = 1L;
			UserRole role = UserRole.CUSTOMER;

			// when
			String token1 = jwtTokenProvider.createRefreshToken(userId, role);
			String token2 = jwtTokenProvider.createRefreshToken(userId, role);

			// then
			String jti1 = jwtTokenProvider.getJti(token1);
			String jti2 = jwtTokenProvider.getJti(token2);

			assertThat(jti1).isNotEqualTo(jti2);
		}
	}

	@Nested
	@DisplayName("validateToken 메서드")
	class ValidateTokenTests {

		@Test
		@DisplayName("성공: 유효한 토큰 검증")
		void validateToken_ValidToken() {
			// given
			String token = jwtTokenProvider.createAccessToken(1L, UserRole.CUSTOMER);

			// when
			boolean isValid = jwtTokenProvider.validateToken(token);

			// then
			assertThat(isValid).isTrue();
		}

		@Test
		@DisplayName("실패: 만료된 토큰")
		void validateToken_ExpiredToken() {
			// given - Create token with very short expiration
			JwtTokenProvider shortExpiryProvider = new JwtTokenProvider();
			ReflectionTestUtils.setField(shortExpiryProvider, "secretKey", secretKey);
			ReflectionTestUtils.setField(shortExpiryProvider, "accessTokenExpiration", -1000L); // Already expired
			ReflectionTestUtils.setField(shortExpiryProvider, "refreshTokenExpiration", refreshTokenExpiration);

			String expiredToken = shortExpiryProvider.createAccessToken(1L, UserRole.CUSTOMER);

			// when
			boolean isValid = jwtTokenProvider.validateToken(expiredToken);

			// then
			assertThat(isValid).isFalse();
		}

		@Test
		@DisplayName("실패: 잘못된 형식의 토큰")
		void validateToken_InvalidFormat() {
			// given
			String invalidToken = "invalid.token.format";

			// when
			boolean isValid = jwtTokenProvider.validateToken(invalidToken);

			// then
			assertThat(isValid).isFalse();
		}

		@Test
		@DisplayName("실패: null 토큰")
		void validateToken_NullToken() {
			// when & then
			assertThatThrownBy(() -> jwtTokenProvider.validateToken(null))
				.isInstanceOf(Exception.class);
		}

		@Test
		@DisplayName("실패: 빈 토큰")
		void validateToken_EmptyToken() {
			// when
			boolean isValid = jwtTokenProvider.validateToken("");

			// then
			assertThat(isValid).isFalse();
		}

		@Test
		@DisplayName("실패: 잘못된 서명의 토큰")
		void validateToken_InvalidSignature() {
			// given - Create token with different secret key
			JwtTokenProvider differentKeyProvider = new JwtTokenProvider();
			String differentKey = "different-secret-key-that-is-also-long-enough-for-hmac-sha-256";
			ReflectionTestUtils.setField(differentKeyProvider, "secretKey", differentKey);
			ReflectionTestUtils.setField(differentKeyProvider, "accessTokenExpiration", accessTokenExpiration);
			ReflectionTestUtils.setField(differentKeyProvider, "refreshTokenExpiration", refreshTokenExpiration);

			String tokenWithDifferentKey = differentKeyProvider.createAccessToken(1L, UserRole.CUSTOMER);

			// when
			boolean isValid = jwtTokenProvider.validateToken(tokenWithDifferentKey);

			// then
			assertThat(isValid).isFalse();
		}
	}

	@Nested
	@DisplayName("getUserAuth 메서드")
	class GetUserAuthTests {

		@Test
		@DisplayName("성공: 토큰에서 사용자 정보 추출")
		void getUserAuth_Success() {
			// given
			Long userId = 123L;
			UserRole role = UserRole.OWNER;
			String token = jwtTokenProvider.createAccessToken(userId, role);

			// when
			UserAuth userAuth = jwtTokenProvider.getUserAuth(token);

			// then
			assertThat(userAuth).isNotNull();
			assertThat(userAuth.getId()).isEqualTo(userId);
			assertThat(userAuth.getUserRole()).isEqualTo(role);
		}

		@Test
		@DisplayName("성공: 다양한 사용자 정보 추출")
		void getUserAuth_DifferentUsers() {
			// given
			Long[] userIds = {1L, 100L, 999L};
			UserRole[] roles = {UserRole.CUSTOMER, UserRole.OWNER, UserRole.MANAGER, UserRole.MASTER};

			// when & then
			for (Long userId : userIds) {
				for (UserRole role : roles) {
					String token = jwtTokenProvider.createAccessToken(userId, role);
					UserAuth userAuth = jwtTokenProvider.getUserAuth(token);

					assertThat(userAuth.getId()).isEqualTo(userId);
					assertThat(userAuth.getUserRole()).isEqualTo(role);
				}
			}
		}

		@Test
		@DisplayName("실패: 유효하지 않은 토큰")
		void getUserAuth_InvalidToken() {
			// given
			String invalidToken = "invalid.token.format";

			// when & then
			assertThatThrownBy(() -> jwtTokenProvider.getUserAuth(invalidToken))
				.isInstanceOf(Exception.class);
		}
	}

	@Nested
	@DisplayName("getJti 메서드")
	class GetJtiTests {

		@Test
		@DisplayName("성공: 리프레시 토큰에서 JTI 추출")
		void getJti_Success() {
			// given
			String refreshToken = jwtTokenProvider.createRefreshToken(1L, UserRole.CUSTOMER);

			// when
			String jti = jwtTokenProvider.getJti(refreshToken);

			// then
			assertThat(jti).isNotNull();
			assertThat(jti).isNotEmpty();
		}

		@Test
		@DisplayName("성공: 액세스 토큰에서 JTI 추출 시도 (null 반환)")
		void getJti_AccessTokenReturnsNull() {
			// given
			String accessToken = jwtTokenProvider.createAccessToken(1L, UserRole.CUSTOMER);

			// when
			String jti = jwtTokenProvider.getJti(accessToken);

			// then
			assertThat(jti).isNull();
		}
	}

	@Nested
	@DisplayName("getExpiration 메서드")
	class GetExpirationTests {

		@Test
		@DisplayName("성공: 토큰의 남은 만료 시간 조회")
		void getExpiration_Success() {
			// given
			String token = jwtTokenProvider.createAccessToken(1L, UserRole.CUSTOMER);

			// when
			long expiration = jwtTokenProvider.getExpiration(token);

			// then
			assertThat(expiration).isPositive();
			assertThat(expiration).isLessThanOrEqualTo(accessTokenExpiration);
		}

		@Test
		@DisplayName("성공: 리프레시 토큰의 만료 시간이 더 길다")
		void getExpiration_RefreshTokenLonger() {
			// given
			String accessToken = jwtTokenProvider.createAccessToken(1L, UserRole.CUSTOMER);
			String refreshToken = jwtTokenProvider.createRefreshToken(1L, UserRole.CUSTOMER);

			// when
			long accessExpiration = jwtTokenProvider.getExpiration(accessToken);
			long refreshExpiration = jwtTokenProvider.getExpiration(refreshToken);

			// then
			assertThat(refreshExpiration).isGreaterThan(accessExpiration);
		}

		@Test
		@DisplayName("성공: 시간이 지나면 만료 시간이 줄어든다")
		void getExpiration_DecreasesOverTime() throws InterruptedException {
			// given
			String token = jwtTokenProvider.createAccessToken(1L, UserRole.CUSTOMER);
			long initialExpiration = jwtTokenProvider.getExpiration(token);

			// when
			Thread.sleep(100); // Wait 100ms
			long laterExpiration = jwtTokenProvider.getExpiration(token);

			// then
			assertThat(laterExpiration).isLessThan(initialExpiration);
		}
	}
}