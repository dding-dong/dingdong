package com.sparta.dingdong.domain.store.service;

import java.util.List;
import java.util.UUID;

import org.springframework.transaction.annotation.Transactional;

import com.sparta.dingdong.common.dto.BaseResponseDto;
import com.sparta.dingdong.common.jwt.UserAuth;
import com.sparta.dingdong.domain.store.dto.request.StoreRequestDto;
import com.sparta.dingdong.domain.store.dto.request.StoreUpdateStatusRequestDto;
import com.sparta.dingdong.domain.store.dto.response.StoreResponseDto;

import jakarta.validation.Valid;

public interface StoreService {
	@Transactional(readOnly = true)
	BaseResponseDto<List<StoreResponseDto>> getActiveStores(UserAuth user);

	@Transactional(readOnly = true)
	BaseResponseDto<List<StoreResponseDto>> getActiveStoresByCategory(UUID storeCategoryId, UserAuth user);

	BaseResponseDto<StoreResponseDto> create(StoreRequestDto req, UserAuth user);

	@Transactional(readOnly = true)
	BaseResponseDto<List<StoreResponseDto>> getMyStores(UserAuth user);

	BaseResponseDto<StoreResponseDto> update(UUID id, StoreRequestDto req, UserAuth user);

	BaseResponseDto<StoreResponseDto> updateStatus(UUID id, StoreUpdateStatusRequestDto req, UserAuth user);

	BaseResponseDto<StoreResponseDto> getMyStore(UUID id, UserAuth user);

	BaseResponseDto<StoreResponseDto> addDeliveryArea(UUID storeId, String dongId, UserAuth user);

	BaseResponseDto<StoreResponseDto> removeDeliveryArea(UUID storeId, UUID deliveryAreaId, UserAuth user);

	@Transactional(readOnly = true)
	BaseResponseDto<List<StoreResponseDto>> getAll(UserAuth user);

	@Transactional(readOnly = true)
	BaseResponseDto<List<StoreResponseDto>> getAllByCategory(UUID storeCategoryId, UserAuth user);

	BaseResponseDto<StoreResponseDto> forceUpdateStatus(UUID id, StoreUpdateStatusRequestDto req,
		UserAuth user);

	BaseResponseDto<StoreResponseDto> manageUpdate(UUID id, @Valid StoreRequestDto req, UserAuth user);

	BaseResponseDto<Void> delete(UUID id, UserAuth user);

	BaseResponseDto<StoreResponseDto> getById(UUID id, UserAuth user);

	BaseResponseDto<StoreResponseDto> restore(UUID storeId, UserAuth user);
}
