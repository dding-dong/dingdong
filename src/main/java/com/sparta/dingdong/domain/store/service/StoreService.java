package com.sparta.dingdong.domain.store.service;

import java.util.List;
import java.util.UUID;

import org.springframework.transaction.annotation.Transactional;

import com.sparta.dingdong.domain.store.dto.StoreDto;

import jakarta.validation.Valid;

public interface StoreService {

	@Transactional(readOnly = true)
	List<StoreDto.Response> getActiveStores();

	@Transactional(readOnly = true)
	List<StoreDto.Response> getActiveStoresByCategory(UUID storeCategoryId);

	StoreDto.Response create(StoreDto.Request req);

	@Transactional(readOnly = true)
	List<StoreDto.Response> getMyStores();

	StoreDto.Response update(UUID id, StoreDto.Request req);

	StoreDto.Response updateStatus(UUID id, StoreDto.UpdateStatusRequest req);

	void delete(UUID id);

	StoreDto.Response getMyStore(UUID id);

	StoreDto.Response addDeliveryArea(UUID storeId, String dongId);

	StoreDto.Response removeDeliveryArea(UUID storeId, UUID deliveryAreaId);

	@Transactional(readOnly = true)
	List<StoreDto.Response> getAll();

	@Transactional(readOnly = true)
	List<StoreDto.Response> getAllByCategory(UUID storeCategoryId);

	StoreDto.Response forceUpdateStatus(UUID id, StoreDto.UpdateStatusRequest req);

	StoreDto.Response manageUpdate(UUID id, StoreDto.@Valid Request req);

	StoreDto.Response getById(UUID id);

	StoreDto.Response restore(UUID storeId);
}
