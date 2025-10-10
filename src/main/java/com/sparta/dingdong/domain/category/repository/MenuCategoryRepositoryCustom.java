package com.sparta.dingdong.domain.category.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sparta.dingdong.domain.category.entity.MenuCategory;

public interface MenuCategoryRepositoryCustom {
	Page<MenuCategory> findByStoreIdWithKeyword(UUID storeId, String keyword, Pageable pageable);
}
