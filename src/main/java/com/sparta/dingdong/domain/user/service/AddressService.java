package com.sparta.dingdong.domain.user.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.dingdong.common.entity.Dong;
import com.sparta.dingdong.common.jwt.UserAuth;
import com.sparta.dingdong.domain.user.dto.request.AddressRequestDto;
import com.sparta.dingdong.domain.user.dto.response.AddressResponseDto;
import com.sparta.dingdong.domain.user.entity.Address;
import com.sparta.dingdong.domain.user.entity.User;
import com.sparta.dingdong.domain.user.exception.AddressNotMatchedException;
import com.sparta.dingdong.domain.user.repository.AddressRepository;
import com.sparta.dingdong.domain.user.repository.DongRepository;
import com.sparta.dingdong.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AddressService {

	private final AddressRepository addressRepository;
	private final DongRepository dongRepository;
	private final UserRepository userRepository;

	@Transactional
	public void addAddress(AddressRequestDto req, UserAuth userAuth) {
		User user = userRepository.findByIdOrElseThrow(userAuth.getId());

		if (req.getIsDefault()) {
			Address.unsetOtherAddress(user);
		}

		Address address = Address.builder()
			.user(user)
			.dong(dongRepository.findByIdOrElseThrow(req.getDongId())) // Dong 조회
			.detailAddress(req.getDetailAddress())
			.postalCode(req.getPostalCode())
			.isDefault(req.getIsDefault())
			.build();
		addressRepository.save(address);
	}

	@Transactional(readOnly = true)
	public List<AddressResponseDto> getAllAddress(UserAuth userAuth) {
		User user = userRepository.findByIdOrElseThrow(userAuth.getId());
		return user.getAddressList().stream()
			.map(AddressResponseDto::of)
			.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<AddressResponseDto> getAddressesByLocation(List<String> cityId, List<String> guId,
		List<String> dongId) {
		List<Address> addresses;

		if (dongId != null && !dongId.isEmpty()) {
			addresses = addressRepository.findByDongIdIn(dongId);
		} else if (guId != null && !guId.isEmpty()) {
			addresses = addressRepository.findByDongGuIdIn(guId);
		} else if (cityId != null && !cityId.isEmpty()) {
			addresses = addressRepository.findByDongGuCityIdIn(cityId);
		} else {
			addresses = addressRepository.findAll();
		}

		return addresses.stream()
			.map(AddressResponseDto::of) // Address → DTO 변환
			.collect(Collectors.toList());
	}

	@Transactional
	public void updateAddress(Long addressId, AddressRequestDto req, UserAuth userAuth) {
		User user = userRepository.findByIdOrElseThrow(userAuth.getId());
		Address address = addressRepository.findByIdOrElseThrow(addressId);

		// 해당 주소가 요청한 유저의 주소인지 확인
		if (!address.getUser().getId().equals(userAuth.getId())) {
			throw new AddressNotMatchedException();
		}

		// 기본주소 처리
		if (req.getIsDefault()) {
			Address.unsetOtherAddress(user);
		}
		Dong dong = dongRepository.findByIdOrElseThrow(req.getDongId());
		address.updateAddress(req, dong);
	}

	@Transactional
	public void deleteAddress(Long addressId, UserAuth userAuth) {
		Address address = addressRepository.findByIdOrElseThrow(addressId);

		// 해당 주소가 요청한 유저의 주소인지 확인
		if (!address.getUser().getId().equals(userAuth.getId())) {
			throw new AddressNotMatchedException();
		}

		addressRepository.delete(address);
	}
}
