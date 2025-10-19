package com.sparta.dingdong.domain.user.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.dingdong.common.dto.BaseResponseDto;
import com.sparta.dingdong.common.jwt.UserAuth;
import com.sparta.dingdong.domain.user.dto.request.AddressRequestDto;
import com.sparta.dingdong.domain.user.dto.response.AddressResponseDto;
import com.sparta.dingdong.domain.user.service.AddressService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/users/address")
@RequiredArgsConstructor
public class AddressControllerV1 {

	private final AddressService addressService;

	@PostMapping
	public ResponseEntity<BaseResponseDto<Void>> addAddress(@Valid @RequestBody AddressRequestDto req,
		@AuthenticationPrincipal UserAuth userAuth) {
		addressService.addAddress(req, userAuth);
		return ResponseEntity.ok(BaseResponseDto.success("주소 등록 완료"));
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('MANAGER','MASTER')")
	public ResponseEntity<BaseResponseDto<List<AddressResponseDto>>> getAddressList(
		@AuthenticationPrincipal UserAuth userAuth) {
		return ResponseEntity.ok(BaseResponseDto.success("주소 조회 완료", addressService.getAllAddress(userAuth)));
	}

	@GetMapping("/search")
	public ResponseEntity<BaseResponseDto<List<AddressResponseDto>>> getAddressesByLocation(
		@RequestParam(required = false) List<String> cityId,
		@RequestParam(required = false) List<String> guId,
		@RequestParam(required = false) List<String> dongId) {

		return ResponseEntity.ok(
			BaseResponseDto.success("주소 조회 완료", addressService.getAddressesByLocation(cityId, guId, dongId)));
	}

	@PatchMapping("/{addressId}")
	public ResponseEntity<BaseResponseDto<Void>> updateAddress(@PathVariable Long addressId,
		@Valid @RequestBody AddressRequestDto req,
		@AuthenticationPrincipal UserAuth userAuth) {
		addressService.updateAddress(addressId, req, userAuth);
		return ResponseEntity.ok(BaseResponseDto.success("주소 수정 완료"));
	}

	@DeleteMapping("/{addressId}")
	public ResponseEntity<BaseResponseDto<Void>> deleteAddress(@PathVariable Long addressId,
		@AuthenticationPrincipal UserAuth userAuth) {
		addressService.deleteAddress(addressId, userAuth);
		return ResponseEntity.ok(BaseResponseDto.success("주소 삭제 완료"));
	}
}
