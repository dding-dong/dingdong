package com.sparta.dingdong.domain.category.repository;

import java.util.Optional;
import java.util.UUID;

import com.sparta.dingdong.domain.category.entity.MenuCategoryItem;

public interface MenuCategoryItemRepositoryCustom {
	Optional<MenuCategoryItem> findByIdWithMenuCategoryAndStore(UUID id);
}
