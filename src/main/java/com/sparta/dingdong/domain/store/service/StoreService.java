package com.sparta.dingdong.domain.store.service;

import java.util.List;
import java.util.UUID;

import org.springframework.transaction.annotation.Transactional;

import com.sparta.dingdong.common.dto.BaseResponseDto;
import com.sparta.dingdong.domain.store.dto.StoreDto;

import jakarta.validation.Valid;

public interface StoreService {
	@Transactional(readOnly = true)
	BaseResponseDto<List<StoreDto.Response>> getActiveStores();

	@Transactional(readOnly = true)
	BaseResponseDto<List<StoreDto.Response>> getActiveStoresByCategory(UUID storeCategoryId);

	BaseResponseDto<StoreDto.Response> create(StoreDto.Request req);

	@Transactional(readOnly = true)
	BaseResponseDto<List<StoreDto.Response>> getMyStores();

	BaseResponseDto<StoreDto.Response> update(UUID id, StoreDto.Request req);

	BaseResponseDto<StoreDto.Response> updateStatus(UUID id, StoreDto.UpdateStatusRequest req);

	BaseResponseDto<StoreDto.Response> getMyStore(UUID id);

	BaseResponseDto<StoreDto.Response> addDeliveryArea(UUID storeId, String dongId);

	BaseResponseDto<StoreDto.Response> removeDeliveryArea(UUID storeId, UUID deliveryAreaId);

	@Transactional(readOnly = true)
	BaseResponseDto<List<StoreDto.Response>> getAll();

	@Transactional(readOnly = true)
	BaseResponseDto<List<StoreDto.Response>> getAllByCategory(UUID storeCategoryId);

	BaseResponseDto<StoreDto.Response> forceUpdateStatus(UUID id, StoreDto.UpdateStatusRequest req);

	BaseResponseDto<StoreDto.Response> manageUpdate(UUID id, StoreDto.@Valid Request req);

	BaseResponseDto<Void> delete(UUID id);

	BaseResponseDto<StoreDto.Response> getById(UUID id);

	BaseResponseDto<StoreDto.Response> restore(UUID storeId);
}
