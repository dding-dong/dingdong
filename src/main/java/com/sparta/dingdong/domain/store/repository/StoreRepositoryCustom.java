package com.sparta.dingdong.domain.store.repository;

import java.util.List;
import java.util.UUID;

import com.sparta.dingdong.domain.store.entity.Store;

public interface StoreRepositoryCustom {
	List<Store> findAll();

	List<Store> findAllActive();

	List<Store> findAllActiveByCategory(UUID categoryId);

	List<Store> findAllByCategory(UUID categoryId);
}
