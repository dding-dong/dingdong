package com.sparta.dingdong.domain.store.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.dingdong.common.jwt.UserAuth;
import com.sparta.dingdong.domain.store.dto.request.StoreRequestDto;
import com.sparta.dingdong.domain.store.dto.request.StoreUpdateStatusRequestDto;
import com.sparta.dingdong.domain.store.dto.response.StoreResponseDto;

import jakarta.validation.Valid;

public interface StoreService {

	@Transactional(readOnly = true)
	Page<StoreResponseDto> getActiveStores(String keyword, Pageable pageable, UserAuth user);

	@Transactional(readOnly = true)
	Page<StoreResponseDto> getActiveStoresByCategory(UUID storeCategoryId, String keyword, Pageable pageable,
		UserAuth user);

	StoreResponseDto create(StoreRequestDto req, UserAuth user);

	@Transactional(readOnly = true)
	Page<StoreResponseDto> getMyStores(String keyword, Pageable pageable, UserAuth user);

	StoreResponseDto update(UUID storeId, StoreRequestDto req, UserAuth user);

	StoreResponseDto updateStatus(UUID storeId, StoreUpdateStatusRequestDto req, UserAuth user);

	StoreResponseDto getMyStore(UUID storeId, UserAuth user);

	StoreResponseDto addDeliveryArea(UUID storeId, String dongId, UserAuth user);

	StoreResponseDto removeDeliveryArea(UUID storeId, UUID deliveryAreaId, UserAuth user);

	@Transactional(readOnly = true)
	Page<StoreResponseDto> getAll(String keyword, Pageable pageable, UserAuth user);

	@Transactional(readOnly = true)
	Page<StoreResponseDto> getAllByCategory(UUID storeCategoryId, String keyword, Pageable pageable,
		UserAuth user);

	StoreResponseDto forceUpdateStatus(UUID storeId, StoreUpdateStatusRequestDto req, UserAuth user);

	StoreResponseDto manageUpdate(UUID storeId, @Valid StoreRequestDto req, UserAuth user);

	void delete(UUID storeId, UserAuth user);

	StoreResponseDto getById(UUID storeId, UserAuth user);

	StoreResponseDto restore(UUID storeId, UserAuth user);
}
