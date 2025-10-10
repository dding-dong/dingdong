package com.sparta.dingdong.domain.store.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sparta.dingdong.domain.store.entity.Store;

public interface StoreRepositoryCustom {

	Page<Store> findAllWithKeyword(String keyword, Pageable pageable, boolean onlyActive);

	Page<Store> findAllActiveWithKeyword(String keyword, Pageable pageable);

	Page<Store> findAllActiveByStoreCategoryWithKeyword(UUID storeCategoryId, String keyword,
		Pageable pageable);

	Page<Store> findByOwnerIdWithKeyword(Long ownerId, String keyword, Pageable pageable);

	Page<Store> findAllWithKeyword(String keyword, Pageable pageable);

	Page<Store> findAllByStoreCategoryWithKeyword(UUID storeCategoryId, String keyword, Pageable pageable);
}
