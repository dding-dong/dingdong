package com.sparta.dingdong.domain.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.dingdong.common.entity.Dong;
import com.sparta.dingdong.common.jwt.JwtUtil;
import com.sparta.dingdong.common.jwt.UserAuth;
import com.sparta.dingdong.domain.user.dto.request.UserCreateRequestDto;
import com.sparta.dingdong.domain.user.dto.request.UserUpdateRequestDto;
import com.sparta.dingdong.domain.user.dto.response.UserResponseDto;
import com.sparta.dingdong.domain.user.entity.Address;
import com.sparta.dingdong.domain.user.entity.Manager;
import com.sparta.dingdong.domain.user.entity.User;
import com.sparta.dingdong.domain.user.entity.enums.ManagerStatus;
import com.sparta.dingdong.domain.user.entity.enums.UserRole;
import com.sparta.dingdong.domain.user.exception.InvalidPasswordException;
import com.sparta.dingdong.domain.user.exception.NicknameNotChangeedException;
import com.sparta.dingdong.domain.user.exception.NoUpdateTargetException;
import com.sparta.dingdong.domain.user.exception.PasswordNotChangedException;
import com.sparta.dingdong.domain.user.repository.AddressRepository;
import com.sparta.dingdong.domain.user.repository.DongRepository;
import com.sparta.dingdong.domain.user.repository.ManagerRepository;
import com.sparta.dingdong.domain.user.repository.RedisRepository;
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
	private final RedisRepository redisRepository;
	private final JwtUtil jwtUtil;

	@Override
	public User findByUser(Long userId) {
		return userRepository.findByIdOrElseThrow(userId);
	}

	@Transactional
	public void createUser(UserCreateRequestDto req) {

		userRepository.validateDuplicateEmail(req.getEmail());
		Dong dong = dongRepository.findByIdOrElseThrow(req.getDongId());
		String encodedPassword = passwordEncoder.encode(req.getPassword());
		User user = User.builder()
			.username(req.getUsername())
			.nickname(req.getNickname())
			.email(req.getEmail())
			.password(encodedPassword)
			.userRole(req.getUserRole())
			.phone(req.getPhone())
			.build();
		userRepository.save(user);
		Address address = Address.builder()
			.user(user)
			.dong(dong)
			.detailAddress(req.getDetailAddress())
			.postalCode(req.getPostalCode())
			.isDefault(true)
			.build();
		addressRepository.save(address);

		if (req.getUserRole() == UserRole.MANAGER) {
			Manager manager = Manager.builder()
				.user(user)
				.managerStatus(ManagerStatus.PENDING)
				.build();
			managerRepository.save(manager);
		}
	}

	@Transactional
	public void updateUser(UserUpdateRequestDto req, UserAuth userAuth) {
		User findUser = userRepository.findByIdOrElseThrow(userAuth.getId());
		checkPassword(req.getOldPassword(), findUser.getPassword());

		if (req.getNickname() == null && req.getNewPassword() == null) {
			throw new NoUpdateTargetException();
		}
		if (req.getNickname() != null && findUser.getNickname().equals(req.getNickname())) {
			throw new NicknameNotChangeedException();
		}
		if (req.getNewPassword() != null && passwordEncoder.matches(req.getNewPassword(), findUser.getPassword())) {
			throw new PasswordNotChangedException();
		}

		String encodedPassword = null;
		if (req.getNewPassword() != null) {
			encodedPassword = passwordEncoder.encode(req.getNewPassword());
		}

		findUser.updateUser(req.getNickname(), encodedPassword);
	}

	public void checkPassword(String rawPassword, String hashedPassword) {
		if (!passwordEncoder.matches(rawPassword, hashedPassword)) {
			throw new InvalidPasswordException();
		}
	}

	@Transactional
	public void deleteUser(UserAuth userAuth) {
		User user = userRepository.findByIdOrElseThrow(userAuth.getId());
		redisRepository.deleteAccessToken(user.getId());
		redisRepository.deleteRefreshToken(user.getId());
		user.softDelete(user.getId());
	}

	public UserResponseDto findById(UserAuth userAuth) {
		User findUser = userRepository.findByIdOrElseThrow(userAuth.getId());
		return UserResponseDto.from(findUser);
	}

	public User findByUser(UserAuth userAuth) {
		return userRepository.findByIdOrElseThrow(userAuth.getId());
	}
}
