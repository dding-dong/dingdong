package com.sparta.dingdong.domain.user.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.sparta.dingdong.common.entity.Dong;
import com.sparta.dingdong.common.jwt.UserAuth;
import com.sparta.dingdong.domain.user.dto.request.UserCreateRequestDto;
import com.sparta.dingdong.domain.user.dto.request.UserUpdateRequestDto;
import com.sparta.dingdong.domain.user.dto.response.UserResponseDto;
import com.sparta.dingdong.domain.user.entity.Address;
import com.sparta.dingdong.domain.user.entity.Manager;
import com.sparta.dingdong.domain.user.entity.User;
import com.sparta.dingdong.domain.user.entity.enums.ManagerStatus;
import com.sparta.dingdong.domain.user.entity.enums.UserRole;
import com.sparta.dingdong.domain.user.exception.DuplicateEmailException;
import com.sparta.dingdong.domain.user.exception.InvalidPasswordException;
import com.sparta.dingdong.domain.user.exception.NicknameNotChangeedException;
import com.sparta.dingdong.domain.user.exception.NoUpdateTargetException;
import com.sparta.dingdong.domain.user.exception.PasswordNotChangedException;
import com.sparta.dingdong.domain.user.exception.UserNotFoundException;
import com.sparta.dingdong.domain.user.repository.AddressRepository;
import com.sparta.dingdong.domain.user.repository.DongRepository;
import com.sparta.dingdong.domain.user.repository.ManagerRepository;
import com.sparta.dingdong.domain.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceImpl 단위 테스트")
class UserServiceImplTest {

	@InjectMocks
	private UserServiceImpl userService;

	@Mock
	private UserRepository userRepository;

	@Mock
	private AddressRepository addressRepository;

	@Mock
	private DongRepository dongRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private ManagerRepository managerRepository;

	private User testUser;
	private Dong testDong;
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
			.addressList(new ArrayList<>())
			.build();

		testDong = Dong.builder()
			.id("11010101")
			.name("청운동")
			.build();

		userAuth = new UserAuth(1L, UserRole.CUSTOMER);
	}

	@Nested
	@DisplayName("createUser 메서드")
	class CreateUserTests {

		private UserCreateRequestDto createRequestDto;

		@BeforeEach
		void setUp() {
			createRequestDto = new UserCreateRequestDto();
			// Use reflection to set private fields
			setField(createRequestDto, "email", "newuser@example.com");
			setField(createRequestDto, "password", "Password123\!");
			setField(createRequestDto, "username", "newuser");
			setField(createRequestDto, "nickname", "newnick");
			setField(createRequestDto, "phone", "010-9876-5432");
			setField(createRequestDto, "userRole", UserRole.CUSTOMER);
			setField(createRequestDto, "cityId", "11");
			setField(createRequestDto, "guId", "010");
			setField(createRequestDto, "dongId", "11010101");
			setField(createRequestDto, "detailAddress", "123번지");
			setField(createRequestDto, "postalCode", "03001");
		}

		@Test
		@DisplayName("성공: CUSTOMER 사용자 생성")
		void createUser_Customer_Success() {
			// given
			given(dongRepository.findByIdOrElseThrow(createRequestDto.getDongId()))
				.willReturn(testDong);
			given(passwordEncoder.encode(createRequestDto.getPassword()))
				.willReturn("encodedPassword");

			// when
			userService.createUser(createRequestDto);

			// then
			ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
			ArgumentCaptor<Address> addressCaptor = ArgumentCaptor.forClass(Address.class);

			verify(userRepository).validateDuplicateEmail(createRequestDto.getEmail());
			verify(userRepository).save(userCaptor.capture());
			verify(addressRepository).save(addressCaptor.capture());
			verify(managerRepository, never()).save(any());

			User savedUser = userCaptor.getValue();
			assertThat(savedUser.getUsername()).isEqualTo("newuser");
			assertThat(savedUser.getNickname()).isEqualTo("newnick");
			assertThat(savedUser.getEmail()).isEqualTo("newuser@example.com");
			assertThat(savedUser.getUserRole()).isEqualTo(UserRole.CUSTOMER);

			Address savedAddress = addressCaptor.getValue();
			assertThat(savedAddress.getDetailAddress()).isEqualTo("123번지");
			assertThat(savedAddress.getPostalCode()).isEqualTo("03001");
			assertThat(savedAddress.isDefault()).isTrue();
		}

		@Test
		@DisplayName("성공: MANAGER 사용자 생성 (Manager 엔티티 추가 생성)")
		void createUser_Manager_Success() {
			// given
			setField(createRequestDto, "userRole", UserRole.MANAGER);
			given(dongRepository.findByIdOrElseThrow(createRequestDto.getDongId()))
				.willReturn(testDong);
			given(passwordEncoder.encode(createRequestDto.getPassword()))
				.willReturn("encodedPassword");

			// when
			userService.createUser(createRequestDto);

			// then
			ArgumentCaptor<Manager> managerCaptor = ArgumentCaptor.forClass(Manager.class);

			verify(userRepository).save(any(User.class));
			verify(addressRepository).save(any(Address.class));
			verify(managerRepository).save(managerCaptor.capture());

			Manager savedManager = managerCaptor.getValue();
			assertThat(savedManager.getManagerStatus()).isEqualTo(ManagerStatus.PENDING);
		}

		@Test
		@DisplayName("성공: OWNER 사용자 생성")
		void createUser_Owner_Success() {
			// given
			setField(createRequestDto, "userRole", UserRole.OWNER);
			given(dongRepository.findByIdOrElseThrow(createRequestDto.getDongId()))
				.willReturn(testDong);
			given(passwordEncoder.encode(createRequestDto.getPassword()))
				.willReturn("encodedPassword");

			// when
			userService.createUser(createRequestDto);

			// then
			verify(userRepository).save(any(User.class));
			verify(addressRepository).save(any(Address.class));
			verify(managerRepository, never()).save(any());
		}

		@Test
		@DisplayName("실패: 중복된 이메일")
		void createUser_DuplicateEmail() {
			// given
			willThrow(new DuplicateEmailException())
				.given(userRepository).validateDuplicateEmail(createRequestDto.getEmail());

			// when & then
			assertThatThrownBy(() -> userService.createUser(createRequestDto))
				.isInstanceOf(DuplicateEmailException.class)
				.hasMessageContaining("중복된 이메일입니다");

			verify(userRepository, never()).save(any());
			verify(addressRepository, never()).save(any());
		}

		@Test
		@DisplayName("실패: 존재하지 않는 동(Dong)")
		void createUser_DongNotFound() {
			// given
			given(dongRepository.findByIdOrElseThrow(createRequestDto.getDongId()))
				.willThrow(new RuntimeException("동을 찾을 수 없습니다"));

			// when & then
			assertThatThrownBy(() -> userService.createUser(createRequestDto))
				.isInstanceOf(RuntimeException.class)
				.hasMessageContaining("동을 찾을 수 없습니다");

			verify(userRepository, never()).save(any());
		}
	}

	@Nested
	@DisplayName("updateUser 메서드")
	class UpdateUserTests {

		@Test
		@DisplayName("성공: 닉네임만 변경")
		void updateUser_NicknameOnly_Success() {
			// given
			UserUpdateRequestDto updateDto = new UserUpdateRequestDto(
				"newNickname",
				"Password123\!",
				null
			);

			given(userRepository.findByIdOrElseThrow(userAuth.getId()))
				.willReturn(testUser);
			given(passwordEncoder.matches(updateDto.getOldPassword(), testUser.getPassword()))
				.willReturn(true);

			// when
			userService.updateUser(updateDto, userAuth);

			// then
			verify(passwordEncoder).matches(updateDto.getOldPassword(), testUser.getPassword());
		}

		@Test
		@DisplayName("성공: 비밀번호만 변경")
		void updateUser_PasswordOnly_Success() {
			// given
			UserUpdateRequestDto updateDto = new UserUpdateRequestDto(
				null,
				"Password123\!",
				"NewPassword456\!"
			);

			given(userRepository.findByIdOrElseThrow(userAuth.getId()))
				.willReturn(testUser);
			given(passwordEncoder.matches(updateDto.getOldPassword(), testUser.getPassword()))
				.willReturn(true);
			given(passwordEncoder.matches(updateDto.getNewPassword(), testUser.getPassword()))
				.willReturn(false);
			given(passwordEncoder.encode(updateDto.getNewPassword()))
				.willReturn("newEncodedPassword");

			// when
			userService.updateUser(updateDto, userAuth);

			// then
			verify(passwordEncoder).encode(updateDto.getNewPassword());
		}

		@Test
		@DisplayName("성공: 닉네임과 비밀번호 모두 변경")
		void updateUser_BothChanged_Success() {
			// given
			UserUpdateRequestDto updateDto = new UserUpdateRequestDto(
				"newNickname",
				"Password123\!",
				"NewPassword456\!"
			);

			given(userRepository.findByIdOrElseThrow(userAuth.getId()))
				.willReturn(testUser);
			given(passwordEncoder.matches(updateDto.getOldPassword(), testUser.getPassword()))
				.willReturn(true);
			given(passwordEncoder.matches(updateDto.getNewPassword(), testUser.getPassword()))
				.willReturn(false);
			given(passwordEncoder.encode(updateDto.getNewPassword()))
				.willReturn("newEncodedPassword");

			// when
			userService.updateUser(updateDto, userAuth);

			// then
			verify(passwordEncoder).encode(updateDto.getNewPassword());
		}

		@Test
		@DisplayName("실패: 변경할 대상이 없음")
		void updateUser_NoUpdateTarget() {
			// given
			UserUpdateRequestDto updateDto = new UserUpdateRequestDto(
				null,
				"Password123\!",
				null
			);

			given(userRepository.findByIdOrElseThrow(userAuth.getId()))
				.willReturn(testUser);
			given(passwordEncoder.matches(updateDto.getOldPassword(), testUser.getPassword()))
				.willReturn(true);

			// when & then
			assertThatThrownBy(() -> userService.updateUser(updateDto, userAuth))
				.isInstanceOf(NoUpdateTargetException.class);
		}

		@Test
		@DisplayName("실패: 닉네임이 변경되지 않음")
		void updateUser_NicknameNotChanged() {
			// given
			UserUpdateRequestDto updateDto = new UserUpdateRequestDto(
				"tester", // Same as current nickname
				"Password123\!",
				null
			);

			given(userRepository.findByIdOrElseThrow(userAuth.getId()))
				.willReturn(testUser);
			given(passwordEncoder.matches(updateDto.getOldPassword(), testUser.getPassword()))
				.willReturn(true);

			// when & then
			assertThatThrownBy(() -> userService.updateUser(updateDto, userAuth))
				.isInstanceOf(NicknameNotChangeedException.class);
		}

		@Test
		@DisplayName("실패: 비밀번호가 변경되지 않음")
		void updateUser_PasswordNotChanged() {
			// given
			UserUpdateRequestDto updateDto = new UserUpdateRequestDto(
				null,
				"Password123\!",
				"Password123\!" // Same as current password
			);

			given(userRepository.findByIdOrElseThrow(userAuth.getId()))
				.willReturn(testUser);
			given(passwordEncoder.matches(updateDto.getOldPassword(), testUser.getPassword()))
				.willReturn(true);
			given(passwordEncoder.matches(updateDto.getNewPassword(), testUser.getPassword()))
				.willReturn(true);

			// when & then
			assertThatThrownBy(() -> userService.updateUser(updateDto, userAuth))
				.isInstanceOf(PasswordNotChangedException.class);
		}

		@Test
		@DisplayName("실패: 잘못된 기존 비밀번호")
		void updateUser_InvalidOldPassword() {
			// given
			UserUpdateRequestDto updateDto = new UserUpdateRequestDto(
				"newNickname",
				"WrongPassword123\!",
				null
			);

			given(userRepository.findByIdOrElseThrow(userAuth.getId()))
				.willReturn(testUser);
			given(passwordEncoder.matches(updateDto.getOldPassword(), testUser.getPassword()))
				.willReturn(false);

			// when & then
			assertThatThrownBy(() -> userService.updateUser(updateDto, userAuth))
				.isInstanceOf(InvalidPasswordException.class);
		}

		@Test
		@DisplayName("실패: 사용자를 찾을 수 없음")
		void updateUser_UserNotFound() {
			// given
			UserUpdateRequestDto updateDto = new UserUpdateRequestDto(
				"newNickname",
				"Password123\!",
				null
			);

			given(userRepository.findByIdOrElseThrow(userAuth.getId()))
				.willThrow(new UserNotFoundException());

			// when & then
			assertThatThrownBy(() -> userService.updateUser(updateDto, userAuth))
				.isInstanceOf(UserNotFoundException.class)
				.hasMessageContaining("유저가 존재하지 않습니다");
		}
	}

	@Nested
	@DisplayName("checkPassword 메서드")
	class CheckPasswordTests {

		@Test
		@DisplayName("성공: 비밀번호 일치")
		void checkPassword_Valid() {
			// given
			String rawPassword = "Password123\!";
			String hashedPassword = "encodedPassword123\!";

			given(passwordEncoder.matches(rawPassword, hashedPassword))
				.willReturn(true);

			// when & then
			assertThatCode(() -> userService.checkPassword(rawPassword, hashedPassword))
				.doesNotThrowAnyException();
		}

		@Test
		@DisplayName("실패: 비밀번호 불일치")
		void checkPassword_Invalid() {
			// given
			String rawPassword = "WrongPassword123\!";
			String hashedPassword = "encodedPassword123\!";

			given(passwordEncoder.matches(rawPassword, hashedPassword))
				.willReturn(false);

			// when & then
			assertThatThrownBy(() -> userService.checkPassword(rawPassword, hashedPassword))
				.isInstanceOf(InvalidPasswordException.class);
		}
	}

	@Nested
	@DisplayName("deleteUser 메서드")
	class DeleteUserTests {

		@Test
		@DisplayName("성공: 사용자 소프트 삭제")
		void deleteUser_Success() {
			// given
			given(userRepository.findByIdOrElseThrow(userAuth.getId()))
				.willReturn(testUser);

			// when
			userService.deleteUser(userAuth);

			// then
			verify(userRepository).findByIdOrElseThrow(userAuth.getId());
		}

		@Test
		@DisplayName("실패: 사용자를 찾을 수 없음")
		void deleteUser_UserNotFound() {
			// given
			given(userRepository.findByIdOrElseThrow(userAuth.getId()))
				.willThrow(new UserNotFoundException());

			// when & then
			assertThatThrownBy(() -> userService.deleteUser(userAuth))
				.isInstanceOf(UserNotFoundException.class)
				.hasMessageContaining("유저가 존재하지 않습니다");
		}
	}

	@Nested
	@DisplayName("findById 메서드")
	class FindByIdTests {

		@Test
		@DisplayName("성공: 사용자 정보 조회")
		void findById_Success() {
			// given
			given(userRepository.findByIdOrElseThrow(userAuth.getId()))
				.willReturn(testUser);

			// when
			UserResponseDto result = userService.findById(userAuth);

			// then
			assertThat(result).isNotNull();
			assertThat(result.getEmail()).isEqualTo("test@example.com");
			assertThat(result.getUsername()).isEqualTo("testuser");
			assertThat(result.getNickname()).isEqualTo("tester");
		}

		@Test
		@DisplayName("실패: 사용자를 찾을 수 없음")
		void findById_UserNotFound() {
			// given
			given(userRepository.findByIdOrElseThrow(userAuth.getId()))
				.willThrow(new UserNotFoundException());

			// when & then
			assertThatThrownBy(() -> userService.findById(userAuth))
				.isInstanceOf(UserNotFoundException.class)
				.hasMessageContaining("유저가 존재하지 않습니다");
		}
	}

	@Nested
	@DisplayName("findByUser 메서드")
	class FindByUserTests {

		@Test
		@DisplayName("성공: User 엔티티 조회")
		void findByUser_Success() {
			// given
			given(userRepository.findByIdOrElseThrow(userAuth.getId()))
				.willReturn(testUser);

			// when
			User result = userService.findByUser(userAuth);

			// then
			assertThat(result).isNotNull();
			assertThat(result.getId()).isEqualTo(1L);
			assertThat(result.getEmail()).isEqualTo("test@example.com");
		}

		@Test
		@DisplayName("실패: 사용자를 찾을 수 없음")
		void findByUser_UserNotFound() {
			// given
			given(userRepository.findByIdOrElseThrow(userAuth.getId()))
				.willThrow(new UserNotFoundException());

			// when & then
			assertThatThrownBy(() -> userService.findByUser(userAuth))
				.isInstanceOf(UserNotFoundException.class)
				.hasMessageContaining("유저가 존재하지 않습니다");
		}
	}

	// Helper method to set private fields using reflection
	private void setField(Object target, String fieldName, Object value) {
		try {
			java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(target, value);
		} catch (Exception e) {
			throw new RuntimeException("Failed to set field: " + fieldName, e);
		}
	}
}