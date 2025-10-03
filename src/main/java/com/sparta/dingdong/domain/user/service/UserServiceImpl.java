package com.sparta.dingdong.domain.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.dingdong.common.entity.Dong;
import com.sparta.dingdong.common.jwt.UserAuth;
import com.sparta.dingdong.domain.user.dto.request.UserCreateRequestDto;
import com.sparta.dingdong.domain.user.dto.response.UserResponseDto;
import com.sparta.dingdong.domain.user.entity.Address;
import com.sparta.dingdong.domain.user.entity.Manager;
import com.sparta.dingdong.domain.user.entity.User;
import com.sparta.dingdong.domain.user.entity.enums.ManagerStatus;
import com.sparta.dingdong.domain.user.entity.enums.UserRole;
import com.sparta.dingdong.domain.user.repository.AddressRepository;
import com.sparta.dingdong.domain.user.repository.DongRepository;
import com.sparta.dingdong.domain.user.repository.ManagerRepository;
import com.sparta.dingdong.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final AddressRepository addressRepository;
	private final DongRepository dongRepository;
	private final PasswordEncoder passwordEncoder;
	private final ManagerRepository managerRepository;

	@Transactional
	public UserResponseDto createUser(UserCreateRequestDto requestDto) {

		// 1. 비밀번호 암호화
		String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

		// 2. User 생성 및 저장
		User user = User.builder()
			.username(requestDto.getUsername())
			.nickname(requestDto.getNickname())
			.email(requestDto.getEmail())
			.password(encodedPassword)
			.userRole(requestDto.getUserRole())
			.phone(requestDto.getPhone())
			.build();

		userRepository.save(user);

		// 3. Dong 조회
		Dong dong = dongRepository.findById(requestDto.getDongId())
			.orElseThrow(() -> new IllegalArgumentException("해당 동을 찾을 수 없습니다."));

		// 4. Address 생성
		Address address = Address.builder()
			.user(user)
			.dong(dong)
			.detailAddress(requestDto.getDetailAddress())
			.postalCode(requestDto.getPostalCode())
			.isDefault(true)
			.build();

		addressRepository.save(address);

		// 5. MANAGER라면 Manager 테이블도 생성
		if (requestDto.getUserRole() == UserRole.MANAGER) {
			Manager manager = Manager.builder()
				.user(user)
				.managerStatus(ManagerStatus.PENDING) // 초기 상태
				.build();
			managerRepository.save(manager); // ManagerRepository 필요
		}

		// 6. ResponseDto 반환
		return UserResponseDto.of(user, address);
	}

	public UserResponseDto findById(UserAuth userAuth) {
		User findUser = userRepository.findByIdOrElseThrow(userAuth.getId());
		return UserResponseDto.from(findUser);
	}
}
