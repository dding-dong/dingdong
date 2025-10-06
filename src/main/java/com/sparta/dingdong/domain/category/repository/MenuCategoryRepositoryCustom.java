package com.sparta.dingdong.domain.category.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.sparta.dingdong.domain.category.entity.MenuCategory;

public interface MenuCategoryRepositoryCustom {
	Optional<MenuCategory> findByIdWithStore(UUID id);

	List<MenuCategory> findByStoreIdWithStore(UUID storeId);
}