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
			user.disableAllDefaultAddress();
		}
		Address address = Address.builder()
			.user(user)
			.dong(dongRepository.findByIdOrElseThrow(req.getDongId()))
			.detailAddress(req.getDetailAddress())
			.postalCode(req.getPostalCode())
			.isDefault(req.getIsDefault())
			.build();
		user.addAddress(address);
		addressRepository.save(address);
	}

	@Transactional(readOnly = true)
	public List<AddressResponseDto> getAllMyAddress(UserAuth userAuth) {
		return addressRepository.findAllByUserIdOrElseThrow(userAuth.getId()).stream()
			.map(AddressResponseDto::of)
			.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<AddressResponseDto> getAddress(List<String> cityId, List<String> guId,
		List<String> dongId) {
		List<Address> addressList;

		if (dongId != null && !dongId.isEmpty()) {
			addressList = addressRepository.findByDongIdIn(dongId);
		} else if (guId != null && !guId.isEmpty()) {
			addressList = addressRepository.findByDongGuIdIn(guId);
		} else if (cityId != null && !cityId.isEmpty()) {
			addressList = addressRepository.findByDongGuCityIdIn(cityId);
		} else {
			addressList = addressRepository.findAll();
		}

		return addressList.stream()
			.map(AddressResponseDto::of)
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
			user.disableAllDefaultAddress();
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

		User user = address.getUser();
		user.deleteAddress(address);
		addressRepository.delete(address);
	}
}
