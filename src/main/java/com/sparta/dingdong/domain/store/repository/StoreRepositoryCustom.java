package com.sparta.dingdong.domain.store.repository;

import java.util.List;
import java.util.UUID;

import com.sparta.dingdong.domain.store.entity.Store;

public interface StoreRepositoryCustom {
	List<Store> findAllActive();

	List<Store> findAllActiveByStoreCategory(UUID categoryId);

	List<Store> findAllByStoreCategory(UUID categoryId);
}
