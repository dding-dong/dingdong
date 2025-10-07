package com.sparta.dingdong.domain.store.service;

import java.util.List;
import java.util.UUID;

import org.springframework.transaction.annotation.Transactional;

import com.sparta.dingdong.common.jwt.UserAuth;
import com.sparta.dingdong.domain.store.dto.request.StoreRequestDto;
import com.sparta.dingdong.domain.store.dto.request.StoreUpdateStatusRequestDto;
import com.sparta.dingdong.domain.store.dto.response.StoreResponseDto;

import jakarta.validation.Valid;

public interface StoreService {

	@Transactional(readOnly = true)
	List<StoreResponseDto> getActiveStores(UserAuth user);

	@Transactional(readOnly = true)
	List<StoreResponseDto> getActiveStoresByCategory(UUID storeCategoryId, UserAuth user);

	StoreResponseDto create(StoreRequestDto req, UserAuth user);

	@Transactional(readOnly = true)
	List<StoreResponseDto> getMyStores(UserAuth user);

	StoreResponseDto update(UUID storeId, StoreRequestDto req, UserAuth user);

	StoreResponseDto updateStatus(UUID storeId, StoreUpdateStatusRequestDto req, UserAuth user);

	StoreResponseDto getMyStore(UUID storeId, UserAuth user);

	StoreResponseDto addDeliveryArea(UUID storeId, String dongId, UserAuth user);

	StoreResponseDto removeDeliveryArea(UUID storeId, UUID deliveryAreaId, UserAuth user);

	@Transactional(readOnly = true)
	List<StoreResponseDto> getAll(UserAuth user);

	@Transactional(readOnly = true)
	List<StoreResponseDto> getAllByCategory(UUID storeCategoryId, UserAuth user);

	StoreResponseDto forceUpdateStatus(UUID storeId, StoreUpdateStatusRequestDto req, UserAuth user);

	StoreResponseDto manageUpdate(UUID storeId, @Valid StoreRequestDto req, UserAuth user);

	void delete(UUID storeId, UserAuth user);

	StoreResponseDto getById(UUID storeId, UserAuth user);

	StoreResponseDto restore(UUID storeId, UserAuth user);
}
