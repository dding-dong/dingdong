package com.sparta.dingdong.common.jwt;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.sparta.dingdong.domain.user.entity.enums.UserRole;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtUtil 단위 테스트")
class JwtUtilTest {

	private JwtUtil jwtUtil;
	private String secretKey;

	@Mock
	private HttpServletRequest httpServletRequest;

	@BeforeEach
	void setUp() {
		jwtUtil = new JwtUtil();
		secretKey = "test-secret-key-that-is-long-enough-for-hmac-sha-256-algorithm-requirement";
		ReflectionTestUtils.setField(jwtUtil, "secretKey", secretKey);
	}

	@Nested
	@DisplayName("createToken 메서드")
	class CreateTokenTests {

		@Test
		@DisplayName("성공: 유효한 액세스 토큰 생성")
		void createToken_Success() {
			// given
			Long userId = 1L;
			UserRole role = UserRole.CUSTOMER;

			// when
			String token = jwtUtil.createToken(userId, role);

			// then
			assertThat(token).isNotNull();
			assertThat(token).isNotEmpty();

			// Verify token contents
			Claims claims = Jwts.parserBuilder()
				.setSigningKey(secretKey.getBytes())
				.build()
				.parseClaimsJws(token)
				.getBody();

			assertThat(claims.getSubject()).isEqualTo("1");
			assertThat(claims.get("userRole", String.class)).isEqualTo("CUSTOMER");
		}

		@Test
		@DisplayName("성공: 다양한 UserRole로 토큰 생성")
		void createToken_AllUserRoles() {
			// given
			Long userId = 1L;

			// when & then
			for (UserRole role : UserRole.values()) {
				String token = jwtUtil.createToken(userId, role);
				UserAuth userAuth = jwtUtil.extractUserAuth(token);

				assertThat(userAuth.getUserRole()).isEqualTo(role);
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
			String token = jwtUtil.createRefreshToken(userId, role);

			// then
			assertThat(token).isNotNull();
			assertThat(token).isNotEmpty();

			// Verify token contents
			Claims claims = Jwts.parserBuilder()
				.setSigningKey(secretKey.getBytes())
				.build()
				.parseClaimsJws(token)
				.getBody();

			assertThat(claims.getSubject()).isEqualTo("1");
			assertThat(claims.get("userRole", String.class)).isEqualTo("CUSTOMER");
			assertThat(claims.get("jti", String.class)).isNotNull();
		}

		@Test
		@DisplayName("성공: 매번 다른 JTI 생성")
		void createRefreshToken_UniqueJti() {
			// given
			Long userId = 1L;
			UserRole role = UserRole.CUSTOMER;

			// when
			String token1 = jwtUtil.createRefreshToken(userId, role);
			String token2 = jwtUtil.createRefreshToken(userId, role);

			// then
			Claims claims1 = Jwts.parserBuilder()
				.setSigningKey(secretKey.getBytes())
				.build()
				.parseClaimsJws(token1)
				.getBody();

			Claims claims2 = Jwts.parserBuilder()
				.setSigningKey(secretKey.getBytes())
				.build()
				.parseClaimsJws(token2)
				.getBody();

			assertThat(claims1.get("jti", String.class))
				.isNotEqualTo(claims2.get("jti", String.class));
		}
	}

	@Nested
	@DisplayName("extractUserAuth 메서드")
	class ExtractUserAuthTests {

		@Test
		@DisplayName("성공: 토큰에서 사용자 정보 추출")
		void extractUserAuth_Success() {
			// given
			Long userId = 123L;
			UserRole role = UserRole.OWNER;
			String token = jwtUtil.createToken(userId, role);

			// when
			UserAuth userAuth = jwtUtil.extractUserAuth(token);

			// then
			assertThat(userAuth).isNotNull();
			assertThat(userAuth.getId()).isEqualTo(userId);
			assertThat(userAuth.getUserRole()).isEqualTo(role);
		}

		@Test
		@DisplayName("실패: 유효하지 않은 토큰")
		void extractUserAuth_InvalidToken() {
			// given
			String invalidToken = "invalid.token.format";

			// when & then
			assertThatThrownBy(() -> jwtUtil.extractUserAuth(invalidToken))
				.isInstanceOf(Exception.class);
		}
	}

	@Nested
	@DisplayName("validateToken 메서드")
	class ValidateTokenTests {

		@Test
		@DisplayName("성공: 유효한 토큰")
		void validateToken_ValidToken() {
			// given
			String token = jwtUtil.createToken(1L, UserRole.CUSTOMER);

			// when
			boolean isValid = jwtUtil.validateToken(token);

			// then
			assertThat(isValid).isTrue();
		}

		@Test
		@DisplayName("실패: 잘못된 형식의 토큰")
		void validateToken_InvalidFormat() {
			// given
			String invalidToken = "invalid.token.format";

			// when
			boolean isValid = jwtUtil.validateToken(invalidToken);

			// then
			assertThat(isValid).isFalse();
		}
	}

	@Nested
	@DisplayName("extractToken 메서드")
	class ExtractTokenTests {

		@Test
		@DisplayName("성공: Bearer 토큰에서 JWT 추출")
		void extractToken_Success() {
			// given
			String jwtToken = "sampleJwtToken123";
			String bearerToken = "Bearer " + jwtToken;
			given(httpServletRequest.getHeader("Authorization")).willReturn(bearerToken);

			// when
			String extractedToken = jwtUtil.extractToken(httpServletRequest);

			// then
			assertThat(extractedToken).isEqualTo(jwtToken);
		}

		@Test
		@DisplayName("실패: Bearer 접두사 없음")
		void extractToken_NoBearerPrefix() {
			// given
			given(httpServletRequest.getHeader("Authorization")).willReturn("InvalidFormat");

			// when
			String extractedToken = jwtUtil.extractToken(httpServletRequest);

			// then
			assertThat(extractedToken).isNull();
		}

		@Test
		@DisplayName("실패: Authorization 헤더 없음")
		void extractToken_NoAuthorizationHeader() {
			// given
			given(httpServletRequest.getHeader("Authorization")).willReturn(null);

			// when
			String extractedToken = jwtUtil.extractToken(httpServletRequest);

			// then
			assertThat(extractedToken).isNull();
		}

		@Test
		@DisplayName("실패: 빈 Authorization 헤더")
		void extractToken_EmptyAuthorizationHeader() {
			// given
			given(httpServletRequest.getHeader("Authorization")).willReturn("");

			// when
			String extractedToken = jwtUtil.extractToken(httpServletRequest);

			// then
			assertThat(extractedToken).isNull();
		}
	}

	@Nested
	@DisplayName("getExpiration 메서드")
	class GetExpirationTests {

		@Test
		@DisplayName("성공: 토큰의 남은 만료 시간 조회")
		void getExpiration_Success() {
			// given
			String token = jwtUtil.createToken(1L, UserRole.CUSTOMER);

			// when
			long expiration = jwtUtil.getExpiration(token);

			// then
			assertThat(expiration).isPositive();
		}

		@Test
		@DisplayName("성공: 시간이 지나면 만료 시간 감소")
		void getExpiration_DecreasesOverTime() throws InterruptedException {
			// given
			String token = jwtUtil.createToken(1L, UserRole.CUSTOMER);
			long initialExpiration = jwtUtil.getExpiration(token);

			// when
			Thread.sleep(100);
			long laterExpiration = jwtUtil.getExpiration(token);

			// then
			assertThat(laterExpiration).isLessThan(initialExpiration);
		}
	}

	@Nested
	@DisplayName("getRefreshExpiration 메서드")
	class GetRefreshExpirationTests {

		@Test
		@DisplayName("성공: 리프레시 토큰의 만료 시간 조회")
		void getRefreshExpiration_Success() {
			// given
			String refreshToken = jwtUtil.createRefreshToken(1L, UserRole.CUSTOMER);

			// when
			long expiration = jwtUtil.getRefreshExpiration(refreshToken);

			// then
			assertThat(expiration).isPositive();
		}

		@Test
		@DisplayName("성공: 리프레시 토큰이 액세스 토큰보다 긴 만료 시간")
		void getRefreshExpiration_LongerThanAccessToken() {
			// given
			String accessToken = jwtUtil.createToken(1L, UserRole.CUSTOMER);
			String refreshToken = jwtUtil.createRefreshToken(1L, UserRole.CUSTOMER);

			// when
			long accessExpiration = jwtUtil.getExpiration(accessToken);
			long refreshExpiration = jwtUtil.getRefreshExpiration(refreshToken);

			// then
			assertThat(refreshExpiration).isGreaterThan(accessExpiration);
		}
	}
}