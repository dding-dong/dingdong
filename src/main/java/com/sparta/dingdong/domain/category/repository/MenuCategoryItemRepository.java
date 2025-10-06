package com.sparta.dingdong.domain.category.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.dingdong.domain.category.entity.MenuCategoryItem;

public interface MenuCategoryItemRepository
	extends JpaRepository<MenuCategoryItem, UUID>, MenuCategoryItemRepositoryCustom {

	List<MenuCategoryItem> findByMenuCategoryIdOrderByOrderNoAsc(UUID menuCategoryId);

	boolean existsByMenuCategoryIdAndOrderNo(UUID categoryId, Integer orderNo);
}