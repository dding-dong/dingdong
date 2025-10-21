package com.sparta.dingdong.domain.user.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.sparta.dingdong.common.entity.Dong;
import com.sparta.dingdong.domain.user.dto.request.UserCreateRequestDto;
import com.sparta.dingdong.domain.user.entity.Address;
import com.sparta.dingdong.domain.user.entity.User;
import com.sparta.dingdong.domain.user.entity.enums.UserRole;
import com.sparta.dingdong.domain.user.repository.AddressRepository;
import com.sparta.dingdong.domain.user.repository.DongRepository;
import com.sparta.dingdong.domain.user.repository.ManagerRepository;
import com.sparta.dingdong.domain.user.repository.RedisRepository;
import com.sparta.dingdong.domain.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

	@Mock
	UserRepository userRepository;

	@Mock
	AddressRepository addressRepository;

	@Mock
	DongRepository dongRepository;

	@Mock
	PasswordEncoder passwordEncoder;

	@Mock
	ManagerRepository managerRepository;

	@Mock
	RedisRepository redisRepository;

	@InjectMocks
	UserServiceImpl userService;

	@DisplayName("정상 테스트1: CUSTOMER 역할의 유저를 생성한다")
	@Test
	void test1() {
		// given
		UserCreateRequestDto req = UserCreateRequestDto.builder()
			.username("홍길동")
			.nickname("길동이")
			.email("customer@example.com")
			.password("Password123!")
			.phone("010-1234-5678")
			.userRole(UserRole.CUSTOMER)
			.cityId("02")
			.guId("23")
			.dongId("400")
			.detailAddress("서울시 강남구 101호")
			.postalCode("06210")
			.build();

		Dong dong = Dong.of("400", "연건동", null);

		when(dongRepository.findByIdOrElseThrow("400")).thenReturn(dong);
		when(passwordEncoder.encode(any())).thenReturn("encodedPassword");

		// when
		userService.createUser(req);

		// then
		verify(userRepository).validateDuplicateEmail("customer@example.com");
		verify(passwordEncoder).encode("Password123!");
		verify(userRepository).save(any(User.class));
		verify(addressRepository).save(any(Address.class));
		verify(managerRepository, never()).save(any()); // CUSTOMER는 매니저 아님

		// 1️⃣ userRepository.save()로 저장되는 User의 필드 검증
		ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
		verify(userRepository).save(userCaptor.capture());
		User savedUser = userCaptor.getValue();

		assertThat(savedUser.getUsername()).isEqualTo("홍길동");
		assertThat(savedUser.getEmail()).isEqualTo("customer@example.com");
		assertThat(savedUser.getUserRole()).isEqualTo(UserRole.CUSTOMER);
		assertThat(savedUser.getPassword()).isEqualTo("encodedPassword");

		// 2️⃣ addressRepository.save()로 저장되는 Address 검증
		ArgumentCaptor<Address> addressCaptor = ArgumentCaptor.forClass(Address.class);
		verify(addressRepository).save(addressCaptor.capture());
		Address savedAddress = addressCaptor.getValue();

		assertThat(savedAddress.getDong().getId()).isEqualTo("400");
		assertThat(savedAddress.getDetailAddress()).isEqualTo("서울시 강남구 101호");
		assertThat(savedAddress.getPostalCode()).isEqualTo("06210");
	}
}
