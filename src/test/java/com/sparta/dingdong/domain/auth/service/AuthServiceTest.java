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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.sparta.dingdong.common.jwt.JwtUtil;
import com.sparta.dingdong.common.jwt.UserAuth;
import com.sparta.dingdong.domain.auth.dto.request.LoginRequestDto;
import com.sparta.dingdong.domain.auth.dto.response.TokenResponse;
import com.sparta.dingdong.domain.auth.exception.AuthErrorCode;
import com.sparta.dingdong.domain.user.entity.User;
import com.sparta.dingdong.domain.user.entity.enums.UserRole;
import com.sparta.dingdong.domain.user.exception.UserErrorCode;
import com.sparta.dingdong.domain.user.repository.RedisRepository;
import com.sparta.dingdong.domain.user.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService 단위 테스트")
class AuthServiceTest {

	@InjectMocks
	private AuthService authService;

	@Mock
	private UserRepository userRepository;

	@Mock
	private RedisRepository redisRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private TokenService tokenService;

	@Mock
	private JwtUtil jwtUtil;

	@Mock
	private HttpServletRequest httpServletRequest;

	private User testUser;
	private LoginRequestDto loginRequestDto;
	private TokenResponse tokenResponse;
	private UserAuth userAuth;

	@BeforeEach
	void setUp() {
		testUser = User.builder()
			.id(1L)
			.username("testuser")
			.nickname("tester")
			.email("test@example.com")
			.password("encodedPassword123\!")
			.userRole(UserRole.CUSTOMER)
			.phone("010-1234-5678")
			.build();

		loginRequestDto = new LoginRequestDto(
			"test@example.com",
			"Password123\!"
		);

		tokenResponse = new TokenResponse(
			"accessToken123",
			"refreshToken123"
		);

		userAuth = new UserAuth(1L, UserRole.CUSTOMER);
	}

	@Nested
	@DisplayName("login 메서드")
	class LoginTests {

		@Test
		@DisplayName("성공: 유효한 이메일과 비밀번호로 로그인")
		void login_Success() {
			// given
			given(userRepository.findByEmailOrElseThrow(loginRequestDto.getEmail()))
				.willReturn(testUser);
			given(passwordEncoder.matches(loginRequestDto.getPassword(), testUser.getPassword()))
				.willReturn(true);
			given(tokenService.generateTokens(testUser.getId(), testUser.getUserRole()))
				.willReturn(tokenResponse);

			// when
			TokenResponse result = authService.login(loginRequestDto);

			// then
			assertThat(result).isNotNull();
			assertThat(result.getAccessToken()).isEqualTo("accessToken123");
			assertThat(result.getRefreshToken()).isEqualTo("refreshToken123");
			verify(tokenService).saveRefreshToken(testUser.getId(), tokenResponse.getRefreshToken());
		}

		@Test
		@DisplayName("실패: 존재하지 않는 이메일")
		void login_UserNotFound() {
			// given
			given(userRepository.findByEmailOrElseThrow(anyString()))
				.willThrow(new RuntimeException("유저가 존재하지 않습니다."));

			// when & then
			assertThatThrownBy(() -> authService.login(loginRequestDto))
				.isInstanceOf(RuntimeException.class)
				.hasMessageContaining("유저가 존재하지 않습니다");
		}

		@Test
		@DisplayName("실패: 잘못된 비밀번호")
		void login_InvalidPassword() {
			// given
			given(userRepository.findByEmailOrElseThrow(loginRequestDto.getEmail()))
				.willReturn(testUser);
			given(passwordEncoder.matches(loginRequestDto.getPassword(), testUser.getPassword()))
				.willReturn(false);

			// when & then
			assertThatThrownBy(() -> authService.login(loginRequestDto))
				.isInstanceOf(RuntimeException.class)
				.hasMessageContaining(UserErrorCode.INVALID_PASSWORD.getMessage());
		}

		@Test
		@DisplayName("실패: 토큰 생성 실패")
		void login_TokenGenerationFailed() {
			// given
			given(userRepository.findByEmailOrElseThrow(loginRequestDto.getEmail()))
				.willReturn(testUser);
			given(passwordEncoder.matches(loginRequestDto.getPassword(), testUser.getPassword()))
				.willReturn(true);
			given(tokenService.generateTokens(testUser.getId(), testUser.getUserRole()))
				.willThrow(new RuntimeException("토큰 생성 실패"));

			// when & then
			assertThatThrownBy(() -> authService.login(loginRequestDto))
				.isInstanceOf(RuntimeException.class)
				.hasMessageContaining("토큰 생성 실패");
		}
	}

	@Nested
	@DisplayName("logout 메서드")
	class LogoutTests {

		@Test
		@DisplayName("성공: 유효한 토큰으로 로그아웃")
		void logout_Success() {
			// given
			String token = "validToken123";
			long expiration = 3600000L;

			given(jwtUtil.extractToken(httpServletRequest)).willReturn(token);
			given(jwtUtil.validateToken(token)).willReturn(true);
			given(jwtUtil.getExpiration(token)).willReturn(expiration);
			given(jwtUtil.extractUserAuth(token)).willReturn(userAuth);

			// when
			authService.logout(httpServletRequest);

			// then
			verify(redisRepository).saveBlackListToken(token, expiration);
			verify(redisRepository).deleteRefreshToken(userAuth.getId());
		}

		@Test
		@DisplayName("성공: null 토큰으로 로그아웃 시도 (예외 없이 종료)")
		void logout_NullToken() {
			// given
			given(jwtUtil.extractToken(httpServletRequest)).willReturn(null);

			// when & then
			assertThatCode(() -> authService.logout(httpServletRequest))
				.doesNotThrowAnyException();
		}

		@Test
		@DisplayName("성공: 유효하지 않은 토큰으로 로그아웃 시도 (예외 없이 종료)")
		void logout_InvalidToken() {
			// given
			String token = "invalidToken";
			given(jwtUtil.extractToken(httpServletRequest)).willReturn(token);
			given(jwtUtil.validateToken(token)).willReturn(false);

			// when & then
			assertThatCode(() -> authService.logout(httpServletRequest))
				.doesNotThrowAnyException();
		}

		@Test
		@DisplayName("성공: 로그아웃 중 예외 발생해도 조용히 처리")
		void logout_ExceptionDuringLogout() {
			// given
			given(jwtUtil.extractToken(httpServletRequest))
				.willThrow(new RuntimeException("토큰 추출 실패"));

			// when & then
			assertThatCode(() -> authService.logout(httpServletRequest))
				.doesNotThrowAnyException();
		}
	}

	@Nested
	@DisplayName("reissue 메서드")
	class ReissueTests {

		@Test
		@DisplayName("성공: 유효한 리프레시 토큰으로 재발급")
		void reissue_Success() {
			// given
			String bearerToken = "Bearer refreshToken123";
			String refreshToken = "refreshToken123";

			given(jwtUtil.validateToken(refreshToken)).willReturn(true);
			given(jwtUtil.extractUserAuth(refreshToken)).willReturn(userAuth);
			given(redisRepository.validateRefreshToken(userAuth.getId(), refreshToken))
				.willReturn(true);
			given(tokenService.generateTokens(userAuth.getId(), userAuth.getUserRole()))
				.willReturn(tokenResponse);

			// when
			TokenResponse result = authService.reissue(bearerToken);

			// then
			assertThat(result).isNotNull();
			assertThat(result.getAccessToken()).isEqualTo("accessToken123");
			assertThat(result.getRefreshToken()).isEqualTo("refreshToken123");
			verify(redisRepository).deleteRefreshToken(userAuth.getId());
			verify(tokenService).saveRefreshToken(userAuth.getId(), tokenResponse.getRefreshToken());
		}

		@Test
		@DisplayName("실패: null 토큰")
		void reissue_NullToken() {
			// when & then
			assertThatThrownBy(() -> authService.reissue(null))
				.isInstanceOf(RuntimeException.class)
				.hasMessageContaining(AuthErrorCode.MISMATCHED_REFRESH_TOKEN.getMessage());
		}

		@Test
		@DisplayName("실패: Bearer 접두사 없는 토큰")
		void reissue_InvalidBearerFormat() {
			// given
			String bearerToken = "refreshToken123";

			// when & then
			assertThatThrownBy(() -> authService.reissue(bearerToken))
				.isInstanceOf(RuntimeException.class)
				.hasMessageContaining(AuthErrorCode.MISMATCHED_REFRESH_TOKEN.getMessage());
		}

		@Test
		@DisplayName("실패: 유효하지 않은 토큰")
		void reissue_InvalidToken() {
			// given
			String bearerToken = "Bearer invalidToken";
			String refreshToken = "invalidToken";

			given(jwtUtil.validateToken(refreshToken)).willReturn(false);

			// when & then
			assertThatThrownBy(() -> authService.reissue(bearerToken))
				.isInstanceOf(RuntimeException.class)
				.hasMessageContaining(AuthErrorCode.MISMATCHED_REFRESH_TOKEN.getMessage());
		}

		@Test
		@DisplayName("실패: Redis에 저장된 리프레시 토큰과 불일치")
		void reissue_MismatchedRefreshToken() {
			// given
			String bearerToken = "Bearer refreshToken123";
			String refreshToken = "refreshToken123";

			given(jwtUtil.validateToken(refreshToken)).willReturn(true);
			given(jwtUtil.extractUserAuth(refreshToken)).willReturn(userAuth);
			given(redisRepository.validateRefreshToken(userAuth.getId(), refreshToken))
				.willReturn(false);

			// when & then
			assertThatThrownBy(() -> authService.reissue(bearerToken))
				.isInstanceOf(RuntimeException.class)
				.hasMessageContaining(AuthErrorCode.REUSED_REFRESH_TOKEN.getMessage());
		}
	}

	@Nested
	@DisplayName("validateStoreOwnership 메서드")
	class ValidateStoreOwnershipTests {

		@Test
		@DisplayName("성공: OWNER가 자신의 가게에 접근")
		void validateStoreOwnership_OwnerAccessesOwnStore() {
			// given
			UserAuth ownerAuth = new UserAuth(1L, UserRole.OWNER);
			Long storeOwnerId = 1L;

			// when & then
			assertThatCode(() -> authService.validateStoreOwnership(ownerAuth, storeOwnerId))
				.doesNotThrowAnyException();
		}

		@Test
		@DisplayName("실패: OWNER가 다른 사람의 가게에 접근")
		void validateStoreOwnership_OwnerAccessesOtherStore() {
			// given
			UserAuth ownerAuth = new UserAuth(1L, UserRole.OWNER);
			Long storeOwnerId = 2L;

			// when & then
			assertThatThrownBy(() -> authService.validateStoreOwnership(ownerAuth, storeOwnerId))
				.isInstanceOf(AccessDeniedException.class)
				.hasMessageContaining("본인 가게만 접근 가능합니다");
		}

		@Test
		@DisplayName("성공: MANAGER가 모든 가게에 접근")
		void validateStoreOwnership_ManagerAccessesAllStores() {
			// given
			UserAuth managerAuth = new UserAuth(1L, UserRole.MANAGER);
			Long storeOwnerId = 2L;

			// when & then
			assertThatCode(() -> authService.validateStoreOwnership(managerAuth, storeOwnerId))
				.doesNotThrowAnyException();
		}

		@Test
		@DisplayName("성공: MASTER가 모든 가게에 접근")
		void validateStoreOwnership_MasterAccessesAllStores() {
			// given
			UserAuth masterAuth = new UserAuth(1L, UserRole.MASTER);
			Long storeOwnerId = 2L;

			// when & then
			assertThatCode(() -> authService.validateStoreOwnership(masterAuth, storeOwnerId))
				.doesNotThrowAnyException();
		}

		@Test
		@DisplayName("성공: CUSTOMER는 체크하지 않음")
		void validateStoreOwnership_CustomerNotChecked() {
			// given
			UserAuth customerAuth = new UserAuth(1L, UserRole.CUSTOMER);
			Long storeOwnerId = 2L;

			// when & then
			assertThatCode(() -> authService.validateStoreOwnership(customerAuth, storeOwnerId))
				.doesNotThrowAnyException();
		}
	}

	@Nested
	@DisplayName("isAdmin 메서드")
	class IsAdminTests {

		@Test
		@DisplayName("true: MASTER 권한")
		void isAdmin_Master() {
			// given
			UserAuth masterAuth = new UserAuth(1L, UserRole.MASTER);

			// when
			boolean result = authService.isAdmin(masterAuth);

			// then
			assertThat(result).isTrue();
		}

		@Test
		@DisplayName("true: MANAGER 권한")
		void isAdmin_Manager() {
			// given
			UserAuth managerAuth = new UserAuth(1L, UserRole.MANAGER);

			// when
			boolean result = authService.isAdmin(managerAuth);

			// then
			assertThat(result).isTrue();
		}

		@Test
		@DisplayName("false: OWNER 권한")
		void isAdmin_Owner() {
			// given
			UserAuth ownerAuth = new UserAuth(1L, UserRole.OWNER);

			// when
			boolean result = authService.isAdmin(ownerAuth);

			// then
			assertThat(result).isFalse();
		}

		@Test
		@DisplayName("false: CUSTOMER 권한")
		void isAdmin_Customer() {
			// given
			UserAuth customerAuth = new UserAuth(1L, UserRole.CUSTOMER);

			// when
			boolean result = authService.isAdmin(customerAuth);

			// then
			assertThat(result).isFalse();
		}

		@Test
		@DisplayName("false: null UserAuth")
		void isAdmin_NullUserAuth() {
			// when
			boolean result = authService.isAdmin(null);

			// then
			assertThat(result).isFalse();
		}
	}

	@Nested
	@DisplayName("ensureAdmin 메서드")
	class EnsureAdminTests {

		@Test
		@DisplayName("성공: MASTER 권한")
		void ensureAdmin_Master() {
			// given
			UserAuth masterAuth = new UserAuth(1L, UserRole.MASTER);

			// when & then
			assertThatCode(() -> authService.ensureAdmin(masterAuth))
				.doesNotThrowAnyException();
		}

		@Test
		@DisplayName("성공: MANAGER 권한")
		void ensureAdmin_Manager() {
			// given
			UserAuth managerAuth = new UserAuth(1L, UserRole.MANAGER);

			// when & then
			assertThatCode(() -> authService.ensureAdmin(managerAuth))
				.doesNotThrowAnyException();
		}

		@Test
		@DisplayName("실패: OWNER 권한")
		void ensureAdmin_Owner() {
			// given
			UserAuth ownerAuth = new UserAuth(1L, UserRole.OWNER);

			// when & then
			assertThatThrownBy(() -> authService.ensureAdmin(ownerAuth))
				.isInstanceOf(AccessDeniedException.class)
				.hasMessageContaining("관리자 권한이 필요합니다");
		}

		@Test
		@DisplayName("실패: CUSTOMER 권한")
		void ensureAdmin_Customer() {
			// given
			UserAuth customerAuth = new UserAuth(1L, UserRole.CUSTOMER);

			// when & then
			assertThatThrownBy(() -> authService.ensureAdmin(customerAuth))
				.isInstanceOf(AccessDeniedException.class)
				.hasMessageContaining("관리자 권한이 필요합니다");
		}

		@Test
		@DisplayName("실패: null UserAuth")
		void ensureAdmin_NullUserAuth() {
			// when & then
			assertThatThrownBy(() -> authService.ensureAdmin(null))
				.isInstanceOf(AccessDeniedException.class)
				.hasMessageContaining("관리자 권한이 필요합니다");
		}
	}
}