package com.sparta.dingdong.domain.category.repository.menucategory;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sparta.dingdong.domain.category.entity.MenuCategoryItem;

public interface MenuCategoryItemRepositoryCustom {
	Optional<MenuCategoryItem> findByIdWithMenuCategoryAndStore(UUID id);

	Page<MenuCategoryItem> findByMenuCategoryIdWithKeyword(UUID menuCategoryId, String keyword,
		Pageable pageable);
}
